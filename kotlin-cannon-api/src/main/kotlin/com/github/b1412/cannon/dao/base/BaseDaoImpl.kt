package com.github.b1412.cannon.dao.base

import com.github.b1412.cannon.entity.User
import com.github.b1412.cannon.jpa.JpaUtil
import com.github.b1412.cannon.service.rule.SecurityFilter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.security.core.context.SecurityContextHolder
import java.io.Serializable
import javax.persistence.EntityManager

@NoRepositoryBean
class BaseDaoImpl<T, ID : Serializable>(
        entityInformation: JpaEntityInformation<T, ID>,
        val entityManager: EntityManager
) : SimpleJpaRepository<T, ID>(entityInformation.javaType, entityManager), BaseDao<T, ID> {


    override fun searchByKeyword(keyword: String, fields: String): List<T> {
        val cb = entityManager.criteriaBuilder
        var query = cb.createQuery(domainClass)
        val root = query.from(domainClass)

        val predicate = fields.split(",")
                .map {
                    cb.like(root.get<String>(it), "%$keyword%")
                }.reduce { a, b ->
                    cb.or(a, b)
                }

        query = query.select(root).where(predicate)
        return entityManager.createQuery(query).resultList
    }

    override fun searchByFilter(filter: Map<String, String>): List<T> {
        log.debug("filter $filter")
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(domainClass)
        val root = query.from(domainClass)
        query.select(root)
        JpaUtil.createPredicate(filter, root, cb).fold({}, { query.where(it) })
        val graph = JpaUtil.createEntityGraphFromURL(entityManager, domainClass, filter)
        return entityManager.createQuery(query).setHint("javax.persistence.fetchgraph", graph).resultList
    }

    companion object {
        private val log = LoggerFactory.getLogger(BaseDaoImpl::class.java)
    }

    override fun searchBySecurity(method: String, requestURI: String, params: Map<String, String>): List<T> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        println(user.grantedAuthorities)
        return searchByFilter(params)
    }
}