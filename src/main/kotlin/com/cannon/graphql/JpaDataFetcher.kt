package com.cannon.graphql

import com.cannon.jpa.JpaUtil
import com.cannon.jpa.QueryBuilder
import graphql.language.Field
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import mu.KotlinLogging
import javax.persistence.EntityManager
import javax.persistence.TypedQuery
import javax.persistence.metamodel.EntityType

private val logger = KotlinLogging.logger {}

open class JpaDataFetcher(
        private var entityManager: EntityManager,
        private var entityType: EntityType<*>) : DataFetcher<Any> {

    override fun get(environment: DataFetchingEnvironment): Any {
        return getQuery(environment, environment.fields.iterator().next()).resultList
    }

    private fun getQuery(environment: DataFetchingEnvironment, field: Field): TypedQuery<*> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery<Any>(entityType.javaType as Class<Any>)
        val root = query.from(entityType)


        val url = QueryBuilder.queryURLFromField(field)
        logger.debug {
            "Query URL $url"
        }
        val filter = QueryBuilder.queryList(url)


        JpaUtil.createPredicate(filter, root, cb).fold({}, { query.where(it) })
        val graph = JpaUtil.createEntityGraphFromURL(entityManager, entityType.javaType, filter)
        return entityManager.createQuery(query).setHint("javax.persistence.fetchgraph", graph)

        //return entityManager.createQuery(query)
    }
}
