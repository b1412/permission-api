package com.github.b1412.api.dao

import arrow.core.Either
import arrow.core.firstOrNone
import arrow.core.getOrElse
import com.github.b1412.jpa.JpaUtil
import org.hibernate.loader.MultipleBagFetchException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import java.io.Serializable
import javax.persistence.EntityGraph
import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root


class BaseDaoImpl<T, ID : Serializable>(
    entityInformation: JpaEntityInformation<T, ID>,
    val entityManager: EntityManager
) : SimpleJpaRepository<T, ID>(entityInformation.javaType, entityManager), BaseDao<T, ID> {

    override fun searchByFilter(filter: Map<String, String>, pageable: Pageable): Page<T> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(domainClass)
        val root = query.from(domainClass)
        query.select(root)
        JpaUtil.createPredicate(filter, root, cb).fold({}, { query.where(it) })
        val spec = Specification { root: Root<T>, query: CriteriaQuery<*>, cb: CriteriaBuilder ->
            val predicates = JpaUtil.createPredicateV2(filter, root, cb).map { listOf(it) }.getOrElse { listOf() }
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
