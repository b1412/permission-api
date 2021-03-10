package com.github.b1412.json

import arrow.core.getOrElse
import arrow.core.toOption
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.github.b1412.api.entity.BaseEntity
import com.github.b1412.permission.util.ClassUtil
import com.github.b1412.permission.util.ClassUtil.allDeclaredFields
import com.github.b1412.util.findClasses
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import java.lang.reflect.ParameterizedType
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JsonReturnHandler : HandlerMethodReturnValueHandler, BeanPostProcessor {
    var advices: List<ResponseBodyAdvice<Any>> = emptyList()

    override fun supportsReturnType(returnType: MethodParameter): Boolean {
        return returnType.annotatedElement.declaredAnnotations.any { it is GraphRender }
    }

    override fun handleReturnValue(
        returnValue: Any?,
        returnType: MethodParameter,
        mavContainer: ModelAndViewContainer,
        webRequest: NativeWebRequest
    ) {
        mavContainer.isRequestHandled = true
        val response = webRequest.getNativeResponse(HttpServletResponse::class.java)!!
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)!!
        val embedded = request.getParameter("embedded")
        val endpoint = (returnType.annotatedElement.declaredAnnotations.first { it is GraphRender }!! as GraphRender)
            .entity

        val objectMapper = ObjectMapper()
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        objectMapper.registerModule(Jdk8Module())
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.registerModule(Hibernate5Module().configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true))
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        val classes = findClasses(BaseEntity::class.java, "classpath*:com/github/b1412/**/*.class") +
                findClasses(BaseEntity::class.java, "classpath*:nz/co/**/*.class")

        if (endpoint == "tree") {
            val entity = request.requestURI.substringAfterLast("/")
            val rootEntityClass = classes.first { it.simpleName.equals(entity, ignoreCase = true) }
            val firstLevelFields = firstLevelFields(rootEntityClass)
            firstLevelFields.add("children")
            val jsonFilter = JacksonJsonFilter(
                fields = mutableMapOf(rootEntityClass to firstLevelFields)
            )
            objectMapper.setFilterProvider(jsonFilter)
            objectMapper.addMixIn(rootEntityClass, jsonFilter.javaClass)
        } else if (endpoint == "tree-parent") {
            val entity = request.requestURI.substringAfter("tree/").substringBeforeLast("/")
            val rootEntityClass = classes.first { it.simpleName.equals(entity, ignoreCase = true) }
            val firstLevelFields = firstLevelFields(rootEntityClass)
            firstLevelFields.add("parent")
            val jsonFilter = JacksonJsonFilter(
                fields = mutableMapOf(rootEntityClass to firstLevelFields)
            )
            objectMapper.setFilterProvider(jsonFilter)
            objectMapper.addMixIn(rootEntityClass, jsonFilter.javaClass)
        } else {
            val rootEntityClass = classes.first { it.simpleName.equals(endpoint, ignoreCase = true) }
            val firstLevelFields = firstLevelFields(rootEntityClass)

            val jsonFilter = JacksonJsonFilter(
                fields = mutableMapOf(rootEntityClass to firstLevelFields)
            )
            objectMapper.setFilterProvider(jsonFilter)
            objectMapper.addMixIn(rootEntityClass, jsonFilter.javaClass)

            val entityClassMap = mutableMapOf<String, Class<*>>()
            embedded
                .toOption()
                .map { it.split(",").toList() }
                .getOrElse { emptyList() }
                .filter { it.isNotBlank() }
                .map { it.split(".").toList() }
                .filter { it.isNotEmpty() }
                .sortedBy { it.size }
                .forEach {
                    if (it.size == 1) { // root node
                        val embeddedNode = it.first()
                        addEmbedded(objectMapper, entityClassMap, jsonFilter, rootEntityClass, embeddedNode)
                    } else {
                        val embeddedNode = it.last()
                        val lastParentNode = it.dropLast(1).last()
                        val parentEntityClass = entityClassMap[lastParentNode]!!
                        addEmbedded(objectMapper, entityClassMap, jsonFilter, parentEntityClass, embeddedNode)
                    }
                }
        }

        response.contentType = MediaType.APPLICATION_JSON_VALUE

        when {
            returnValue is ByteArray -> {
                println("byte array result")
            }
            returnValue!!.javaClass == ResponseEntity::class.java -> {
                val typedValue = returnValue as ResponseEntity<*>
                val json: String = objectMapper.writeValueAsString(typedValue.body)
                response.status = typedValue.statusCode.value()
                response.writer.write(json)
            }
            else -> {
                val json: String = objectMapper.writeValueAsString(returnValue)
                response.writer.write(json)
            }
        }
    }

    private fun addEmbedded(
        objectMapper: ObjectMapper,
        entityClassMap: MutableMap<String, Class<*>>,
        jsonFilter: JacksonJsonFilter,
        entityClass: Class<*>,
        embeddedNode: String
    ) {
        val embeddedFields = entityClass.allDeclaredFields().first { it.name == embeddedNode }
        val genericType = embeddedFields.genericType
        val embeddedClazz: Class<*>
        embeddedClazz = when (genericType) {
            is ParameterizedType -> Class.forName(genericType.actualTypeArguments.first().typeName)
            else -> genericType as Class<*>
        }

        entityClassMap.putIfAbsent(embeddedNode, embeddedClazz)
        val embeddedFirstLevelFields = firstLevelFields(embeddedClazz)
        jsonFilter.fields[entityClass]!!.add(embeddedFields.name)
        jsonFilter.fields[embeddedClazz] = embeddedFirstLevelFields
        objectMapper.addMixIn(embeddedClazz, jsonFilter.javaClass)

        // add subclass
        val subTypes = embeddedClazz.getDeclaredAnnotationsByType(JsonSubTypes::class.java)
        if (subTypes.isNotEmpty()) {
            val embeddedSubClasses = subTypes.first().value
            embeddedSubClasses.forEach {
                entityClassMap.putIfAbsent("$embeddedNode<${it.value.simpleName}>", it.value.java)
                val subEmbeddedFirstLevelFields = firstLevelFields(it.value.java)
                // jsonFilter.fields[entityClass]!!.add(embeddedFields.name)
                jsonFilter.fields[it.value.java] = subEmbeddedFirstLevelFields
                objectMapper.addMixIn(it.value.java, jsonFilter.javaClass)

            }
        }
        //
    }


    private fun firstLevelFields(entityClass: Class<*>): MutableList<String> {
        return ClassUtil.firstLevelFields(entityClass).map { it.name }.toMutableList()
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
