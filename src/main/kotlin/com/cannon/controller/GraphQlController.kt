package com.cannon.controller

import com.cannon.graphql.GraphQLExecutor
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.ExecutionResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class GraphQlController(
        val graphQLExecutor: GraphQLExecutor,

        val objectMapper: ObjectMapper
) {


    class GraphQLInputQuery {
        var query: String? = null
        var variables: String? = null
    }

    @PostMapping("/graphql")
    fun graphQl(@RequestBody query: GraphQLInputQuery): ExecutionResult {
        return when {
            query.variables.isNullOrEmpty() -> graphQLExecutor.execute(query.query!!, null)
            else -> {
                val variables = objectMapper.readValue(query.variables, Map::class.java) as Map<String, Any>?
                graphQLExecutor.execute(query.query!!, variables)
            }
        }

    }


}

