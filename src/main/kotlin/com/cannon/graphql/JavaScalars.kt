package com.cannon.graphql

import graphql.language.IntValue
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import org.slf4j.LoggerFactory
import java.text.DateFormat
import java.text.ParseException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import java.util.*

object JavaScalars {
    internal val log = LoggerFactory.getLogger(JavaScalars::class.java)

    var GraphQLLocalDateTime = GraphQLScalarType("LocalDateTime", "Date type", object : Coercing<Any, Any> {
        override fun serialize(input: Any): Any? {
            if (input is String) {
                return parseStringToLocalDateTime(input)
            } else if (input is LocalDateTime) {
                return input
            } else if (input is Long) {
                return parseLongToLocalDateTime(input)
            } else if (input is Int) {
                return parseLongToLocalDateTime(input.toLong())
            }
            return null
        }

        override fun parseValue(input: Any): Any? {
            return serialize(input)
        }

        override fun parseLiteral(input: Any): Any? {
            if (input is StringValue) {
                return parseStringToLocalDateTime(input.value)
            } else if (input is IntValue) {
                val value = input.value
                return parseLongToLocalDateTime(value.toLong())
            }
            return null
        }

        private fun parseLongToLocalDateTime(input: Long): LocalDateTime {
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(input), TimeZone.getDefault().toZoneId())
        }

        private fun parseStringToLocalDateTime(input: String): LocalDateTime? {
            try {
                return LocalDateTime.parse(input)
            } catch (e: DateTimeParseException) {
                log.warn("Failed to parse Date from input: $input", e)
                return null
            }

        }
    })

    var GraphQLInstant = GraphQLScalarType("Instant", "Date type", object : Coercing<Instant, Long> {

        override fun serialize(input: Any): Long? {
            if (input is Instant) {
                return input.epochSecond
            }
            throw CoercingSerializeException(
                    "Expected type 'Instant' but was '" + input.javaClass.simpleName + "'.")
        }

        override fun parseValue(input: Any): Instant {
            if (input is Long) {
                return Instant.ofEpochSecond(input)
            } else if (input is Int) {
                return Instant.ofEpochSecond(input.toLong())
            }
            throw CoercingSerializeException(
                    "Expected type 'Long' or 'Integer' but was '" + input.javaClass.simpleName + "'.")
        }

        override fun parseLiteral(input: Any): Instant? {
            return if (input is IntValue) {
                Instant.ofEpochSecond(input.value.toLong())
            } else null
        }

    })

    var GraphQLLocalDate = GraphQLScalarType("LocalDate", "Date type", object : Coercing<Any, Any> {
        override fun serialize(input: Any): Any? {
            if (input is String) {
                return parseStringToLocalDate(input)
            } else if (input is LocalDate) {
                return input
            } else if (input is Long) {
                return parseLongToLocalDate(input)
            } else if (input is Int) {
                return parseLongToLocalDate(input.toLong())
            }
            return null
        }

        override fun parseValue(input: Any): Any? {
            return serialize(input)
        }

        override fun parseLiteral(input: Any): Any? {
            if (input is StringValue) {
                return parseStringToLocalDate(input.value)
            } else if (input is IntValue) {
                val value = input.value
                return parseLongToLocalDate(value.toLong())
            }
            return null
        }

        private fun parseLongToLocalDate(input: Long): LocalDate {
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(input), TimeZone.getDefault().toZoneId()).toLocalDate()
        }

        private fun parseStringToLocalDate(input: String): LocalDate? {
            try {
                return LocalDate.parse(input)
            } catch (e: DateTimeParseException) {
                log.warn("Failed to parse Date from input: $input", e)
                return null
            }

        }
    })

    var GraphQLDate = GraphQLScalarType("Date", "Date type", object : Coercing<Any, Any> {

        override fun serialize(input: Any): Any? {
            return when (input) {
                is String -> parseStringToDate(input)
                is Date -> input
                is Long -> Date(input.toLong())
                is Int -> Date(input.toLong())
                else -> null
            }
        }

        override fun parseValue(input: Any): Any? {
            return serialize(input)
        }

        override fun parseLiteral(input: Any): Any? {
            if (input is StringValue) {
                return parseStringToDate(input.value)
            } else if (input is IntValue) {
                val value = input.value
                return Date(value.toLong())
            }
            return null
        }

        private fun parseStringToDate(input: String): Date? {
            try {
                return DateFormat.getInstance().parse(input)
            } catch (e: ParseException) {
                log.warn("Failed to parse Date from input: $input", e)
                return null
            }

        }
    })

    var GraphQLUUID = GraphQLScalarType("UUID", "UUID type", object : Coercing<Any, Any> {

        override fun serialize(input: Any): Any? {
            return if (input is UUID) {
                input
            } else null
        }

        override fun parseValue(input: Any): Any? {
            return if (input is String) {
                parseStringToUUID(input)
            } else null
        }

        override fun parseLiteral(input: Any): Any? {
            return if (input is StringValue) {
                parseStringToUUID(input.value)
            } else null
        }

        private fun parseStringToUUID(input: String): UUID? {
            try {
                return UUID.fromString(input)
            } catch (e: IllegalArgumentException) {
                log.warn("Failed to parse UUID from input: $input", e)
                return null
            }

        }
    })
}
