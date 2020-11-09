package com.github.b1412.permission.jpa

import arrow.core.extensions.list.foldable.firstOption
import graphql.language.*
import graphql.parser.Parser

object QueryBuilder {
    fun queryList(queryUrl: String): Map<String, String> {
        return queryUrl
                .split("&")
                .map { Pair(it.substringBefore("="), it.substringAfter("=")) }
                .groupBy { it.first }
                .map {
                    when (it.value.size) {
                        1 -> {
                            val f = it.value.first()
                            f.first to f.second
                        }
                        else -> {
                            it.key to it.value.joinToString(",")
                        }
                    }
                }.toMap()
    }

    fun graphqlPayload(input: String): String {
        val document = Parser().parseDocument(input)
        val rootField = (document.definitions[0].children[0] as SelectionSet).selections[0] as Field
        return queryURLFromField(rootField)
    }

    fun queryURLFromField(rootField: Field): String {
        val embedded = mutableListOf<String>()
        val field = mutableListOf<String>()
        val arguments = rootField.arguments
        val queryString = arguments.firstOption { it.name == "where" }.fold(
                { "" },
                { argument ->
                    val filterFields = (argument.value as ObjectValue).objectFields
                    filterFields.joinToString("&") { f ->
                        val (name, op) = f.name.split("_")
                        val value = f.value
                        val oldQuery = when (value) {
                            is StringValue -> {
                                "f_$name=${value.value}"
                            }
                            is IntValue -> {
                                "f_$name=${value.value}"
                            }
                            is ArrayValue -> {
                                "f_$name=${value.values.joinToString(",") { (it as StringValue).value }}"
                            }
                            else -> throw  IllegalArgumentException("unknown value type")
                        }
                        "$oldQuery&f_${name}_op=$op"
                    }
                })
        val nodes = rootField.selectionSet.selections
        nodes.forEach {
            val name = (it as Field).name
            if (it.children.isNotEmpty()) {
                val childrenNodes = (it.children[0] as SelectionSet).selections
                val (e, f) = fieldAndEmbedded(name, childrenNodes)
                embedded += e
                field.addAll(f)
            } else {
                field += name
            }
        }


        val embeddedStr = embedded.joinToString(",")
        val fieldsStr = field.joinToString(",")
        val url = "embedded=$embeddedStr&fields=$fieldsStr"
        return "$queryString&$url"
    }

    private fun fieldAndEmbedded(aliasOrKey: String, children: List<Selection<Selection<*>>>): Pair<List<String>, List<String>> {
        val (fields, embedded) = children.partition { (it as Field).selectionSet == null }
        var first = mutableListOf(aliasOrKey)
        val secord = fields.map { aliasOrKey + "." + (it as Field).name }.toMutableList()
        if (embedded.isNotEmpty() && embedded[0].children != null) {
            val (embedded2, fields2) = fieldAndEmbedded(aliasOrKey + "." + (embedded[0] as Field).name, (embedded[0].children[0] as SelectionSet).selections)
            first.addAll(embedded2)
            secord += fields2
        }

        return Pair(first, secord)
    }

}
