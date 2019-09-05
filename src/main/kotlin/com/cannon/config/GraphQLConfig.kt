package com.cannon.config

import com.cannon.graphql.GraphQLExecutor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GraphQLConfig {
    @Bean
    fun graphQLExecutor(): GraphQLExecutor {
        return GraphQLExecutor()
    }
}