package com.github.b1412.permission.graphql

import graphql.language.IntValue
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*

object JavaScalars {
    var GraphQLZonedDateTime = GraphQLScalarType("ZonedDateTime", "Date type", object : Coercing<Any, Any> {
        override fun serialize(input: Any): Any? {
            return when (input) {
                is ZonedDateTime -> input
                else -> null
            }
        }

        override fun parseValue(input: Any): Any? {
            return serialize(input)
        }

        override fun parseLiteral(input: Any): Any? {
            if (input is StringValue) {
                return parseStringToZonedDateTime(input.value)
            } else if (input is IntValue) {
                val value = input.value
                return parseLongToZonedDateTime(value.toLong())
            }
            return null
        }

        private fun parseLongToZonedDateTime(input: Long): ZonedDateTime {
            return ZonedDateTime.ofInstant(Instant.ofEpochSecond(input), TimeZone.getDefault().toZoneId())
        }

        private fun parseStringToZonedDateTime(input: String): ZonedDateTime? {
            return ZonedDateTime.parse(input)
        }
    })
}
