package com.github.b1412.cannon.dao.base

import arrow.core.getOrElse
import com.github.b1412.cannon.jpa.JpaUtil
import com.github.b1412.cannon.jpa.UrlMapper
import com.github.b1412.cannon.jpa.V1UrlMapper
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.stereotype.Component
import java.io.Serializable
import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

@Component
class BaseDaoImpl<T, ID : Serializable>(
        entityInformation: JpaEntityInformation<T, ID>,
        val entityManager: EntityManager
) : SimpleJpaRepository<T, ID>(entityInformation.javaType, entityManager), BaseDao<T, ID> {

    override fun searchByFilter(filter: Map<String, String>, pageable: Pageable,urlMapper: UrlMapper): Page<T> {
        log.debug("filter $filter")
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(domainClass)
        val root = query.from(domainClass)
        query.select(root)
       // JpaUtil.createPredicate(filter, root, cb).fold({}, { query.where(it) })
        //val graph = JpaUtil.createEntityGraphFromURL(entityManager, domainClass, filter)
        val spec = Specification { root: Root<T>, query: CriteriaQuery<*>, cb: CriteriaBuilder ->
            val predicates = JpaUtil.createPredicate(filter, root, cb).map { listOf(it) }.getOrElse { listOf() }
            query.where(*predicates.toTypedArray())
            query.restriction
        }
        return findAll(spec, pageable)
       // return entityManager.createQuery(query).setHint("javax.persistence.fetchgraph", graph).resultList
    }

    companion object {
        private val log = LoggerFactory.getLogger(BaseDaoImpl::class.java)
    }
}