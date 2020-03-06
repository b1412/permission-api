package com.github.b1412.cannon.graphql

import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.schema.GraphQLSchema
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.transaction.Transactional

@Component
class GraphQLExecutor(
        var builder: GraphQLSchema.Builder
) {
    var graphQL: GraphQL? = null

    @PostConstruct
    @Synchronized
    protected fun createGraphQL() {
        this.graphQL = GraphQL.newGraphQL(builder.build()).build()
    }

    @Transactional
    fun execute(query: String, arguments: Map<String, Any>?): ExecutionResult {
        return if (arguments == null)
            graphQL!!.execute(query)
        else
            graphQL!!.execute(ExecutionInput.newExecutionInput().query(query).variables(arguments).build())
    }
}
