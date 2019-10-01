package com.cannon.json

import arrow.core.getOrElse
import arrow.core.toOption
import com.cannon.entity.BaseEntity
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import java.lang.reflect.ParameterizedType
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JsonReturnHandler : HandlerMethodReturnValueHandler, BeanPostProcessor {
    var advices: List<ResponseBodyAdvice<Any>> = emptyList()


    override fun supportsReturnType(returnType: MethodParameter): Boolean {
        return returnType.annotatedElement.declaredAnnotations.any { it is GetMapping }
    }

    override fun handleReturnValue(returnValue: Any?, returnType: MethodParameter, mavContainer: ModelAndViewContainer, webRequest: NativeWebRequest) {
        mavContainer.isRequestHandled = true
        val response = webRequest.getNativeResponse(HttpServletResponse::class.java)!!
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)!!

        val root = request.requestURI
        val embedded = request.getParameter("embedded")
        val clazzName = BaseEntity::class.java.`package`.name + "." + root.substring(1).substringBefore("/").capitalize()
        try {
            Class.forName(clazzName)
        } catch (ex: Exception) {
            return
        }
        val rootEntityClass = Class.forName(clazzName)
        val firstLevelFields = fieldsOfClass(rootEntityClass)
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(Jdk8Module())
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val jsonFilter = JacksonJsonFilter(
                fields = mutableMapOf(rootEntityClass to firstLevelFields)
        )
        val entityClassMap = mutableMapOf<String, Class<*>>()

        objectMapper.setFilterProvider(jsonFilter)
        objectMapper.addMixIn(rootEntityClass, jsonFilter.javaClass)

        embedded.toOption()
                .map { it.split(",").toList() }
                .getOrElse { emptyList() }
                .filter { it.isNotBlank() }
                .map { e -> e.split(".").toList() }
                .filter { it.isNotEmpty() }
                .sortedBy { it.size }
                .forEach {
                    if (it.size == 1) { //root node
                        val embeddedNode = it.first()
                        addEmbedded(objectMapper, entityClassMap, jsonFilter, rootEntityClass, embeddedNode)
                    } else {
                        val embeddedNode = it.last()
                        val lastParentNode = it.dropLast(1).last()
                        val parentEntityClass = entityClassMap[lastParentNode]!!
                        addEmbedded(objectMapper, entityClassMap, jsonFilter, parentEntityClass, embeddedNode)
                    }
                }

        val json = objectMapper.writeValueAsString(returnValue)
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write(json)
    }

    private fun addEmbedded(objectMapper: ObjectMapper, entityClassMap: MutableMap<String, Class<*>>, jsonFilter: JacksonJsonFilter, entityClass: Class<*>, embeddedNode: String) {
        val embeddedFields = entityClass.declaredFields.first { it.name == embeddedNode }
        val genericType = embeddedFields.genericType
        val embeddedClazz: Class<*>
        embeddedClazz = when (genericType) {
            is ParameterizedType -> Class.forName(genericType.actualTypeArguments.first().typeName)
            else -> genericType as Class<*>
        }

        entityClassMap.putIfAbsent(embeddedNode, embeddedClazz)
        val embeddedFirstLevelFields = fieldsOfClass(embeddedClazz)
        jsonFilter.fields[entityClass]!!.add(embeddedFields.name)
        jsonFilter.fields[embeddedClazz] = embeddedFirstLevelFields
        objectMapper.addMixIn(embeddedClazz, jsonFilter.javaClass)
    }


    private fun fieldsOfClass(entityClass: Class<*>): MutableList<String> {
        return (entityClass.declaredFields + entityClass.superclass.declaredFields).filter { field ->
            field.annotations.isEmpty() || field.annotations.all { annotation ->
                (annotation is OneToMany || annotation is OneToOne || annotation is ManyToOne).not()
            }
        }.map { it.name }.toMutableList()
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        if (bean is ResponseBodyAdvice<*>) {
            advices += (bean as ResponseBodyAdvice<Any>)
        } else if (bean is RequestMappingHandlerAdapter) {
            var handlers = bean.returnValueHandlers!!
            var jsonHandler: JsonReturnHandler? = null
            for (i in 0 until handlers.size) {
                val handler = handlers[i]
                if (handler is JsonReturnHandler) {
                    jsonHandler = handler
                    break
                }
            }
            if (jsonHandler != null) {
                handlers = handlers.filter { it != jsonHandler }
                handlers = listOf(jsonHandler) + handlers
                bean.returnValueHandlers = handlers // change the jsonhandler sort
            }
        }
        return bean
    }

}