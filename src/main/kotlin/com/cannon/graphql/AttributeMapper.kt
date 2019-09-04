package com.cannon.graphql

import graphql.schema.GraphQLType
import java.util.*

/**
 * (Functional) Interface to map Classes to GraphQLTypes.
 */
@FunctionalInterface
interface AttributeMapper {

    /**
     * Returns the GraphQLType for the given Class.  If this mapper doesn't know how to handle this particular class,
     * it MUST return an empty Optional.
     *
     * @param javaType
     * @return
     */
    fun getBasicAttributeType(javaType: Class<*>): Optional<GraphQLType>

}
