package com.cannon.graphql

import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.schema.GraphQLSchema

import javax.annotation.PostConstruct
import javax.annotation.Resource
import javax.persistence.EntityManager
import javax.transaction.Transactional

/**
 * A GraphQL executor capable of constructing a [GraphQLSchema] from a JPA [EntityManager]. The executor
 * uses the constructed schema to execute queries directly from the JPA data source.
 *
 *
 * If the executor is given a mutator function, it is feasible to manipulate the [GraphQLSchema], introducing
 * the option to add mutations, subscriptions etc.
 */
open class GraphQLExecutor {

    @Resource
    private var entityManager: EntityManager? = null
    private var graphQL: GraphQL? = null
    /**
     * @return The [GraphQLSchema] used by this executor.
     */
    var graphQLSchema: GraphQLSchema? = null
        private set
    /**
     * Gets the builder that was used to create the Schema that this executor is basing its query executions on. The
     * builder can be used to update the executor with the [.updateSchema] method.
     * @return An instance of a builder.
     */
    var builder: GraphQLSchema.Builder? = null
        private set

    constructor() {
        createGraphQL(null)
    }

    /**
     * Creates a read-only GraphQLExecutor using the entities discovered from the given [EntityManager].
     *
     * @param entityManager The entity manager from which the JPA classes annotated with
     * [javax.persistence.Entity] is extracted as [GraphQLSchema] objects.
     */
    constructor(entityManager: EntityManager) {
        this.entityManager = entityManager
        createGraphQL(null)
    }

    /**
     * Creates a read-only GraphQLExecutor using the entities discovered from the given [EntityManager].
     *
     * @param entityManager The entity manager from which the JPA classes annotated with
     * [javax.persistence.Entity] is extracted as [GraphQLSchema] objects.
     * @param attributeMappers Custom [AttributeMapper] list, if you need any non-standard mappings.
     */
    constructor(entityManager: EntityManager, attributeMappers: Collection<AttributeMapper>) {
        this.entityManager = entityManager
        createGraphQL(attributeMappers)
    }

    @PostConstruct
    @Synchronized
    protected fun createGraphQL() {
        createGraphQL(null)
    }

    @Synchronized
    protected fun createGraphQL(attributeMappers: Collection<AttributeMapper>?) {
        if (entityManager != null) {
            if (builder == null && attributeMappers == null) {
                this.builder = GraphQLSchemaBuilder(entityManager!!)
            } else if (builder == null) {
                this.builder = GraphQLSchemaBuilder(entityManager!!, attributeMappers!!)
            }
            this.graphQLSchema = builder!!.build()
            this.graphQL = GraphQL.newGraphQL(graphQLSchema).build()
        }
    }

    @Transactional
    open fun execute(query: String): ExecutionResult {
        return graphQL!!.execute(query)
    }

    @Transactional
    open fun execute(query: String, arguments: Map<String, Any>?): ExecutionResult {
        return if (arguments == null) graphQL!!.execute(query) else graphQL!!.execute(ExecutionInput.newExecutionInput().query(query).variables(arguments).build())
    }

    /**
     * Returns the schema that this executor bases its queries on.
     * @return An instance of a [GraphQLSchema].
     */
    fun getSchema(): GraphQLSchema? {
        return graphQLSchema
    }

    /**
     * Uses the given builder to re-create and replace the [GraphQLSchema]
     * that this executor uses to execute its queries.
     *
     * @param builder The builder to recreate the current [GraphQLSchema] and [GraphQL] instances.
     * @return The same executor but with a new [GraphQL] schema.
     */
    fun updateSchema(builder: GraphQLSchema.Builder): GraphQLExecutor {
        this.builder = builder
        createGraphQL(null)
        return this
    }

    /**
     * Uses the given builder to re-create and replace the [GraphQLSchema]
     * that this executor uses to execute its queries.
     *
     * @param builder The builder to recreate the current [GraphQLSchema] and [GraphQL] instances.
     * @param attributeMappers Custom [AttributeMapper] list, if you need any non-standard mappings.
     * @return The same executor but with a new [GraphQL] schema.
     */
    fun updateSchema(builder: GraphQLSchema.Builder, attributeMappers: Collection<AttributeMapper>): GraphQLExecutor {
        this.builder = builder
        createGraphQL(attributeMappers)
        return this
    }

}
