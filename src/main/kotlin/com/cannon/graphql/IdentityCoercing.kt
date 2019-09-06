package com.cannon.graphql

import graphql.schema.Coercing

class IdentityCoercing : Coercing<Any, Any> {
    override fun serialize(input: Any): Any {
        return input
    }

    override fun parseValue(input: Any): Any {
        return input
    }

    override fun parseLiteral(input: Any): Any {
        return input
    }

}
