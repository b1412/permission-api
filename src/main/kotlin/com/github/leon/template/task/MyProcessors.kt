package com.github.leon.template.task

import arrow.syntax.collections.tail
import com.github.leon.generator.entity.CodeEntity
import com.github.leon.generator.entity.CodeField
import com.github.leon.generator.entity.CodeProject
import com.github.leon.generator.entity.Task
import com.github.leon.generator.ext.Utils
import kotlin.coroutines.experimental.buildSequence

val entityCache: MutableMap<String, MutableMap<String, Any>> = mutableMapOf()


val ngxProjectCache: MutableMap<String, MutableMap<String, Any>> = mutableMapOf()


val projectPermissionProcessor: (Task, CodeProject) -> Map<String, Any> = { _, project ->

    var map = ngxProjectCache[project.name]
    if (map == null) {


        val menus = project.entities.flatMap { it.menus }.asSequence().filter { it.disable.not() }
                .groupBy { it.parentName }
                .map { entry ->
                    Pair(entry.key, entry.value.sortedBy { it.sort })
                }.toList().toMap()
        map = mutableMapOf(
                "menus" to menus
        )
        ngxProjectCache[project.name] = map
    }
    map

}

val ngxProcessor: (Task, CodeEntity) -> Map<String, Any> = { task, entity ->
    // 获取不重复的services，带嵌套entity的字段为一个注入的service

    var map = entityCache[entity.name]
    if (map == null) {
        val ignoreFields = listOf("ATTACHMENT", "ROLE", "USER", "GRANTEDAUTHORITY", "BRANCH", null, entity.name)
        val services = entity.fields.filter {
            ignoreFields.all { field -> it.type.element.equals(field, ignoreCase = true).not() }
        }.distinctBy { it.type.element }
        val fields = entity.fields
        val (formHiddenFields, otherFields) = entity.fields.partition { it.hiddenInForm || it.primaryKey }
        val groupedFields = groupFields(otherFields).takeWhile { it.isNotEmpty() }.toList()
        val subFormFields = groupFields(otherFields.filter { it.hiddenInSubForm.not() }).takeWhile { it.isNotEmpty() }.toList()
        //val listFields: List<CodeField> = fields.partition { it.hiddenInList }.second
        val listFields: List<CodeField> = fields//.partition { it.hiddenInList }.second

        val requiredFields = fields.filter { it.required }
        val listEmbeddedString = (fields.filter { listOf("Entity", "List").any { field -> it.type.name == field } }
                .filter { it.name != "grantedAuthorities" && it.name != "child" && it.name != "parent" }
                .map { it.name } + entity.embeddedEntity.map { it.name }).joinToString(",") { Utils.lowerHyphen(it) }
        val selectOneFields = fields.filter { it.selectOne!! }
                .filter { !it.type.element.equals("USER", ignoreCase = true) }
                .filter { !it.type.element.equals(entity.name, ignoreCase = true) }
                .distinctBy { it.type.element }

        val excelFields = fields.filter { it.importable }

        map = mutableMapOf(
                "ngClass" to "",
                "services" to services,
                "subFormFields" to subFormFields,
                "formHiddenFields" to formHiddenFields,
                "listFields" to listFields,
                "selectOneFields" to selectOneFields,
                "groupedFields" to groupedFields,
                "excelFields" to excelFields,
                "searchFields" to fields.filter { it.searchable },
                "requiredFields" to requiredFields,
                "listEmbeddedString" to listEmbeddedString,
                "formEmbeddedString" to listEmbeddedString
        )
        entityCache[entity.name] = map
    }
    map

}


fun groupFields(codeFields: List<CodeField>): Sequence<List<CodeField>> = buildSequence {
    var terms = codeFields
    while (true) {
        when {
            terms.isEmpty() -> {
                yield(emptyList())
                terms = emptyList()
            }
            terms.tail().isEmpty() -> {
                yield(listOf(terms.first()))
                terms = terms.tail()
            }
            terms.first().weight + terms.tail().first().weight > 12 -> {
                yield(listOf(terms.first()))
                terms = terms.tail()
            }
            terms.first().weight + terms.tail().first().weight == 12 -> {
                yield(listOf(terms.first(), terms.tail().first()))
                terms = terms.tail().tail()
            }
        }
    }
}
