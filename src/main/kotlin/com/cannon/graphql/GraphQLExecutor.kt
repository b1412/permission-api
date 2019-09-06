package com.cannon.graphql

import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.schema.GraphQLSchema
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Component
class GraphQLExecutor(
        val entityManager: EntityManager
) {
    var graphQL: GraphQL? = null
    var graphQLSchema: GraphQLSchema? = null
    var builder: GraphQLSchema.Builder? = null

    @PostConstruct
    @Synchronized
    protected fun createGraphQL() {
        this.builder = GraphQLSchemaBuilder(entityManager)
        this.graphQLSchema = builder!!.build()
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build()
    }

    @Transactional
    fun execute(query: String, arguments: Map<String, Any>?): ExecutionResult {
        return if (arguments == null) graphQL!!.execute(query) else graphQL!!.execute(ExecutionInput.newExecutionInput().query(query).variables(arguments).build())
    }

}
