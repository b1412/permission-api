package com.github.b1412.permission.controller

import com.github.b1412.permission.graphql.GraphQLExecutor
import graphql.ExecutionResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class GraphQlController(
    val graphQLExecutor: GraphQLExecutor
) {
    class GraphQLInputQuery {
        var query: String? = null
    }

    @PostMapping("/graphql")
    fun graphQl(@RequestBody query: GraphQLInputQuery): ExecutionResult {
        return graphQLExecutor.execute(query.query!!, null)
    }
}


