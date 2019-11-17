package com.github.b1412.cannon.json

import com.fasterxml.jackson.annotation.JsonFilter
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.BeanPropertyFilter
import com.fasterxml.jackson.databind.ser.FilterProvider
import com.fasterxml.jackson.databind.ser.PropertyFilter
import com.fasterxml.jackson.databind.ser.PropertyWriter
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter

@JsonFilter("JacksonFilter")
class JacksonJsonFilter(
        val fields: MutableMap<Class<*>, MutableList<String>> = mutableMapOf()
) : FilterProvider() {

    override fun findFilter(filterId: Any): BeanPropertyFilter {
        throw UnsupportedOperationException("Access to deprecated filters not supported")
    }

    override fun findPropertyFilter(filterId: Any, valueToFilter: Any?): PropertyFilter {
        return object : SimpleBeanPropertyFilter() {
            override fun serializeAsField(pojo: Any, jgen: JsonGenerator, prov: SerializerProvider, writer: PropertyWriter) {
                val name = writer.name
                if (apply(pojo, name)) {
                    writer.serializeAsField(pojo, jgen, prov)
                } else if (!jgen.canOmitFields()) {
                    writer.serializeAsOmittedField(pojo, jgen, prov)
                }
            }
        }
    }

    fun apply(pojo: Any, name: String): Boolean {
        return fields[pojo::class.java]!!.contains(name)
    }
}