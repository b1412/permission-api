package com.cannon.jpa

import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.toOption
import graphql.language.Field
import mu.KotlinLogging
import javax.persistence.EntityGraph
import javax.persistence.EntityManager
import javax.persistence.Subgraph
import javax.persistence.criteria.*

private val logger = KotlinLogging.logger {}


object JpaUtil {
    fun <T> createPredicate(filter: Map<String, String>, root: Root<T>, cb: CriteriaBuilder): Option<Predicate> {
        val filterFields = filter.filter {
            it.key.contains("f_") && !it.key.endsWith("_op")
        }
        if (filterFields.isEmpty()) {
            return Option.empty()
        } else {
            val predicates = filterFields.map {
                val value = it.value
                val operator = filter[it.key + "_op"].toOption().getOrElse { "=" }
                val field = it.key.replace("f_", "")
                val searchPath: Path<String>
                val fields = field.split(".")
                if (fields.size > 1) {
                    var join: Join<Any, Any> = root.join<Any, Any>(fields[0])
                    for (i in 1 until fields.size - 1) {
                        join = join.join(fields[i])
                    }
                    searchPath = join.get(fields[fields.size - 1])
                } else {
                    searchPath = root.get<String>(field)
                }
                when (operator) {
                    "like" -> cb.like(searchPath, "%$value%")
                    "in" -> searchPath.`in`(value.split(","))
                    // "between" -> cb.between(searchPath, value.split(",")[0], value.split(",")[1])
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
                .map {
                    it.split(",")
                }
                .getOrElse {
                    listOf()
                }
        logger.debug {
            embedded
        }
        val maxLevel = embedded.map { it.split(".").size }.max()
        logger.debug { "$maxLevel maxLevel" }
        val graph = entityManager.createEntityGraph(domainClass)

        when (maxLevel) {
            0 -> graph
            1 -> {
                embedded.forEach {
                    graph.addAttributeNodes(it)
                }
            }
            2 -> {
                val subGraphs: MutableMap<String, Subgraph<Any>> = mutableMapOf()
                embedded.sortedBy { it.split(".").size }
                        .forEach {
                            val nodes = it.split(".")
                            when (nodes.size) {
                                1 -> {
                                    subGraphs.putIfAbsent(nodes[0], graph.addSubgraph(nodes[0]))
                                }
                                2 -> {
                                    val subgraph = subGraphs.get(nodes[0])!!
                                    subgraph.addAttributeNodes(nodes[1])
                                }
                            }
                        }
                println(subGraphs)
            }
            else -> graph
        }

        return graph
    }

    fun <T> createEntityGraphFromGraphQL(entityManager: EntityManager, domainClass: Class<T>, field: Field): EntityGraph<T> {
        println(field)
        val graph = entityManager.createEntityGraph(domainClass)

        return graph
    }


}
