package com.github.b1412.permission.graphql


import arrow.core.extensions.list.foldable.firstOption
import arrow.core.getOrElse
import com.github.b1412.jpa.JpaUtil
import com.github.b1412.permission.jpa.QueryBuilder
import graphql.language.*
import graphql.schema.*
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.TypedQuery
import javax.persistence.metamodel.Attribute
import javax.persistence.metamodel.EntityType
import javax.persistence.metamodel.PluralAttribute
import kotlin.math.ceil

open class JpaDataFetcher(
        private var entityManager: EntityManager,
        private var entityType: EntityType<*>) : DataFetcher<Any> {

    override fun get(environment: DataFetchingEnvironment): Any {
        val field = environment.fields.iterator().next()
        val result = LinkedHashMap<String, Any>()
        val pageInformation = extractPageInformation(environment, field)
        val totalPagesSelection = getSelectionField(field, "totalPages")
        val totalElementsSelection = getSelectionField(field, "totalElements")
        val contentSelection = getSelectionField(field, "content")
        if (contentSelection.isPresent) {
            val typedQuery = getQuery(environment, field, contentSelection.get())
            result["content"] = typedQuery
                    .setFirstResult((pageInformation.page!! - 1) * pageInformation.size!!)
                    .setMaxResults(pageInformation.size!!).resultList
        }

        if (totalElementsSelection.isPresent || totalPagesSelection.isPresent) {
            val totalElements = contentSelection
                    .map { contentField -> getCountQuery(environment, contentField).singleResult }
                    .orElseGet { getCountQuery(environment, Field()).singleResult }

            result["totalElements"] = totalElements
            result["totalPages"] = ceil(totalElements!! / pageInformation.size!!.toDouble()).toLong()
        }
        return result
    }

    private fun getQuery(environment: DataFetchingEnvironment, rootField: Field, contentField: Field): TypedQuery<*> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery<Any>(entityType.javaType as Class<Any>)
        val root = query.from(entityType)
        val url = QueryBuilder.queryURLFromField(rootField).substringBefore("&embedded") + QueryBuilder.queryURLFromField(contentField)
        val filter = QueryBuilder.queryList(url)
        JpaUtil.createPredicate(filter, root, cb).fold({}, { query.where(it) })
        val graph = JpaUtil.createEntityGraphFromURL(entityManager, entityType.javaType, filter)
        return entityManager.createQuery(query).setHint("javax.persistence.fetchgraph", graph)
    }

    private fun getJavaType(environment: DataFetchingEnvironment, argument: Argument): Class<*> {
        val argumentEntityAttribute = getAttribute(environment, argument)
        return if (argumentEntityAttribute is PluralAttribute<*, *, *>) argumentEntityAttribute.elementType.javaType else argumentEntityAttribute.javaType
    }


    private fun convertValue(environment: DataFetchingEnvironment, argument: Argument, value: Value<*>): Any {
        when (value) {
            is StringValue -> {
                val convertedValue = environment.getArgument<Any>(argument.name)
                return when {
                    convertedValue != null -> convertedValue
                    else -> value.value
                }
            }
            is VariableReference -> return environment.arguments[value.name]!!
            is ArrayValue -> return value.values.map { convertValue(environment, argument, it) }
            is EnumValue -> {
                val enumType = getJavaType(environment, argument)
                return TODO()
                //return Enum.valueOf<Enum>(enumType, value.name)
            }
            is IntValue -> return value.value
            is BooleanValue -> return value.isValue
            is FloatValue -> return value.value
            else -> return value.toString()
        }
    }

    private fun getCountQuery(environment: DataFetchingEnvironment, field: Field): TypedQuery<Long> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Long::class.java)
        val root = query.from(entityType)
        val idAttribute = entityType.getId(Any::class.java)
        query.select(cb.count(root.get<Any>(idAttribute.name)))
        val predicates = field.arguments.map { cb.equal(root.get<Any>(it.name), convertValue(environment, it, it.value)) }
        query.where(*predicates.toTypedArray())
        return entityManager.createQuery(query)
    }

    private fun getSelectionField(field: Field, fieldName: String): Optional<Field> {
        return field.selectionSet.selections.stream().filter { it is Field }.map { it as Field }.filter { it -> fieldName == it.name }.findFirst()
    }

    private fun extractPageInformation(environment: DataFetchingEnvironment, field: Field): PageInformation {
        val paginationRequest = field.arguments.stream().filter { "pageRequest" == it.name }.findFirst()
        if (paginationRequest.isPresent) {
            field.arguments.remove(paginationRequest.get())
            val paginationValues = paginationRequest.get().value as ObjectValue
            val page = paginationValues.objectFields.firstOption { "page" == it.name }.map { (it.value as IntValue).value.toInt() }.getOrElse { 1 }
            val size = paginationValues.objectFields.firstOption { "size" == it.name }.map { (it.value as IntValue).value.toInt() }.getOrElse { 20 }
            return PageInformation(page, size)
        }
        return PageInformation(1, Integer.MAX_VALUE)
    }

    private fun getAttribute(environment: DataFetchingEnvironment, argument: Argument): Attribute<*, *> {
        val objectType = getObjectType(environment, argument)
        val entityType = getEntityType(objectType)

        return entityType.getAttribute(argument.name)
    }

    private fun getEntityType(objectType: GraphQLObjectType?): EntityType<*> {
        return entityManager.metamodel.entities.stream().filter { it.name == objectType!!.name }.findFirst().get()
    }

    private fun getObjectType(environment: DataFetchingEnvironment, argument: Argument): GraphQLObjectType? {
        var outputType: GraphQLType = environment.fieldType
        if (outputType is GraphQLList)
            outputType = outputType.wrappedType

        return if (outputType is GraphQLObjectType) outputType else null
    }

    private class PageInformation(var page: Int?, var size: Int?)
}
