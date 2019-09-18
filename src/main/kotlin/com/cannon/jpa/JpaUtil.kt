package com.cannon.jpa

import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.toOption
import graph.EntityGraphs
import graph.GraphParser
import mu.KotlinLogging
import javax.persistence.EntityGraph
import javax.persistence.EntityManager
import javax.persistence.criteria.*
import javax.persistence.metamodel.EntityType
import javax.persistence.metamodel.PluralAttribute

private val logger = KotlinLogging.logger {}

object JpaUtil {
    fun <T> createPredicate(filter: Map<String, String>, root: Root<T>, cb: CriteriaBuilder): Option<Predicate> {
        val filterFields = filter.filter {
            it.key.contains("f_") && !it.key.endsWith("_op")
        }
        if (filterFields.isEmpty()) {
            return Option.empty()
        } else {
            val entityType = root.model
            println(entityType)

            val predicates = filterFields.map {
                val value = it.value
                val operator = filter[it.key + "_op"].toOption().getOrElse { "=" }
                val field = it.key.replace("f_", "")
                val searchPath: Path<Any>
                val fields = field.split(".")
                var javaType: Class<*>? = null
                if (fields.size > 1) {//embedded field
                    var join: Join<Any, Any> = root.join<Any, Any>(fields[0])
                    for (i in 1 until fields.size - 1) {
                        join = join.join(fields[i])
                        javaType = getJavaType(entityType, fields[i]) //FIXME
                    }

                    searchPath = join.get(fields[fields.size - 1])
                } else {
                    javaType = getJavaType(entityType, field)
                    searchPath = root.get<Any>(field)
                }
                logger.debug { "javaType $javaType" }
                when (operator) {
                    "like" -> {
                        cb.like(searchPath as Path<String>, "%$value%")
                    }
                    "in" -> {
                        searchPath.`in`(*value.split(",").toTypedArray())
                    }
                    "between" -> {
                        if (javaType!!.isAssignableFrom(java.lang.Long::class.java)) {
                            val (lowerbond, upperbond) = value.split(",").map { v ->
                                v.toLong()
                            }
                            cb.between(searchPath as Path<Long>, lowerbond, upperbond)
                        } else {
                            throw  IllegalArgumentException()
                        }
                    }
                    else -> cb.equal(searchPath, value)
                }
            }.reduce { l, r ->
                cb.and(l, r)
            }
            return predicates.toOption()
        }
    }

    fun <T> createEntityGraphFromURL(entityManager: EntityManager, domainClass: Class<T>, filter: Map<String, String>): EntityGraph<T> {
        val embedded = filter["embedded"]
                .toOption()
                .filter { it.isNotBlank() }
                .map { it.split(",") }
                .getOrElse {
                    listOf()
                }
        logger.debug { "embedded $embedded" }
        val graphs = embedded.map {
            it.split(".").reversed().reduce { l, r ->
                val s = "$r($l)"
                s
            }
        }.map {
            GraphParser.parse(domainClass, it, entityManager)
        }
        val graph = EntityGraphs.merge(entityManager, domainClass, *graphs.toTypedArray())
        return graph
    }

    private fun getJavaType(entityType: EntityType<*>, field: String): Class<*> {
        val argumentEntityAttribute = entityType.getAttribute(field)
        return if (argumentEntityAttribute is PluralAttribute<*, *, *>) argumentEntityAttribute.elementType.javaType else argumentEntityAttribute.javaType
    }
}
