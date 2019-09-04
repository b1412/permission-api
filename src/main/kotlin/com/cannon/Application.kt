package com.cannon

import com.cannon.graphql.GraphQLExecutor
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application {
    @Bean
    fun graphQLExecutor(): GraphQLExecutor {
        return GraphQLExecutor()
    }

}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}