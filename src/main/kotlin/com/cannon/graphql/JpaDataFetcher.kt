package com.cannon.graphql

import com.cannon.jpa.JpaUtil
import graphql.language.Field
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import javax.persistence.EntityManager
import javax.persistence.TypedQuery
import javax.persistence.metamodel.EntityType

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

        val graph =  JpaUtil.createEntityGraphFromGraphQL(entityManager,entityType.javaType,field)
        return entityManager.createQuery(query).setHint("javax.persistence.fetchgraph", graph)

        //return entityManager.createQuery(query)
    }
}
