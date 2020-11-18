package com.github.b1412.api.dao

import arrow.core.Either
import arrow.core.firstOrNone
import arrow.core.getOrElse
import com.github.b1412.jpa.JpaUtil
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import java.io.Serializable
import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root

class BaseDaoImpl<T, ID : Serializable>(
        entityInformation: JpaEntityInformation<T, ID>,
        val entityManager: EntityManager
) : SimpleJpaRepository<T, ID>(entityInformation.javaType, entityManager), BaseDao<T, ID> {

    override fun searchByFilter(filter: Map<String, String>, pageable: Pageable): Page<T> {
        log.debug("filter $filter")
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(domainClass)
        val root = query.from(domainClass)
        query.select(root)
        // JpaUtil.createPredicate(filter, root, cb).fold({}, { query.where(it) })
//        val graph = JpaUtil.createEntityGraphFromURL(entityManager, domainClass, filter)
        val spec = Specification { root: Root<T>, query: CriteriaQuery<*>, cb: CriteriaBuilder ->
            val predicates = JpaUtil.createPredicateV2(filter, root, cb).map { listOf(it) }.getOrElse { listOf() }
            query.where(*predicates.toTypedArray())
            query.restriction
        }
        return findAll(spec, pageable)
        // return entityManager.createQuery(query).setHint("javax.persistence.fetchgraph", graph).resultList
    }

    companion object {
        private val log = LoggerFactory.getLogger(BaseDaoImpl::class.java)
    }

    override fun searchOneBy(filter: Map<String, String>): Either<Unit, T> {
        return searchByFilter(filter, Pageable.unpaged()).firstOrNone().toEither { Unit }
    }
}
