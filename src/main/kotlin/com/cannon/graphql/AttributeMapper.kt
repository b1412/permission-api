package com.cannon.graphql

import graphql.schema.GraphQLType
import java.util.*

@FunctionalInterface
interface AttributeMapper {
    fun getBasicAttributeType(javaType: Class<*>): Optional<GraphQLType>
}
