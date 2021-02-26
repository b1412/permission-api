package com.github.b1412.api.dao

import arrow.core.Either
import arrow.core.firstOrNone
import arrow.core.getOrElse
import com.github.b1412.jpa.JpaUtil
import org.hibernate.loader.MultipleBagFetchException
import org.joor.Reflect
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import java.io.Serializable
import javax.persistence.EntityGraph
import javax.persistence.EntityManager
import javax.persistence.criteria.*
import javax.persistence.metamodel.EntityType

class BaseDaoImpl<T, ID : Serializable>(
    entityInformation: JpaEntityInformation<T, ID>,
    val entityManager: EntityManager
) : SimpleJpaRepository<T, ID>(entityInformation.javaType, entityManager), BaseDao<T, ID> {

    override fun searchByFilter(filter: Map<String, String>, pageable: Pageable): Page<T> {
        var filterM = filter
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(domainClass)
        val root = query.from(domainClass)
        query.select(root)
        val tree = filter.entries.firstOrNull { it.key.contains("_tree") }
        if (tree != null) {
            var entityType = root.model
            val field = tree.key.substringBefore("_tree")
            val fields = field.split(".")
            if (fields.size > 1) {
                var join: Join<Any, Any> = root.join(fields[0], JoinType.LEFT)
                for (i in 1 until fields.size - 1) {
                    join = join.join(fields[i], JoinType.LEFT)
                }
                fields.windowed(2, 1).forEach { (e, f) ->
                    entityType = JpaUtil.getReferenceEntityType(entityType, e)
                }
            }
            val newField = field + "_in"
            val newFilter = filter.toMutableMap()
            newFilter.remove(tree.key)
            val list = getChildren(entityType, "parent.id", tree.value)
            val ids = (list.map { Reflect.on(it).get<Any>("id") } + tree.value).joinToString(",")
            newFilter[newField] = ids
            filterM = newFilter
        }
        val spec = Specification { root: Root<T>, query: CriteriaQuery<*>, cb: CriteriaBuilder ->
            val predicates = JpaUtil.createPredicateV2(filterM, root, cb).map { listOf(it) }.getOrElse { listOf() }
            query.where(*predicates.toTypedArray())
            query.restriction
        }
        val graphResult = runCatching { JpaUtil.createEntityGraphFromURL(entityManager, domainClass, filter) }
        return when {
            graphResult.isSuccess -> {
                findAll(spec, pageable, graphResult.getOrNull()!!)
            }
            else -> {
                findAll(spec, pageable)
            }
        }
    }

    fun getChildren(entityType: EntityType<T>, field: String, value: String): MutableList<Any?> {
        val sql = "from $entityType where $field = $value"
        val list: MutableList<Any?> = entityManager.createQuery(sql).resultList
        val subList = list.flatMap {
            getChildren(entityType, field, Reflect.on(it).get<Long>("id").toString())
        }.toMutableList()
        list.addAll(subList)
        return list
    }

    override fun searchOneBy(filter: Map<String, String>): Either<Unit, T> {
        return searchByFilter(filter, Pageable.unpaged()).firstOrNone().toEither { }
    }

    override fun findAll(spec: Specification<T>, pageable: Pageable, entityGraphType: EntityGraph<T>): Page<T> {
        val query = getQuery(spec, pageable.sort)
        query.setHint("javax.persistence.fetchgraph", entityGraphType)
        val result = kotlin.runCatching {
            if (pageable.isUnpaged)
                PageImpl(query.resultList)
            else
                readPage(query, domainClass, pageable, spec)
        }
        return if (result.isFailure) {
            when (val cause = result.exceptionOrNull()!!.cause!!) {
                is MultipleBagFetchException -> findAll(spec, pageable)
                else -> throw cause
            }
        } else {
            result.getOrNull()!!
        }
    }

    override fun searchOneByOrNull(filter: Map<String, String>): T? {
        return searchByFilter(filter, Pageable.unpaged()).firstOrNull()
    }
}