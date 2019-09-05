package com.cannon.graphql

import com.cannon.graphql.annotation.GraphQLIgnore
import com.cannon.graphql.annotation.SchemaDocumentation
import graphql.Scalars
import graphql.schema.*
import mu.KotlinLogging
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Member
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.metamodel.*


private val logger = KotlinLogging.logger {}

class GraphQLSchemaBuilder : GraphQLSchema.Builder {

    private val entityManager: EntityManager
    private val classCache = HashMap<Class<*>, GraphQLType>()
    private val embeddableCache = HashMap<EmbeddableType<*>, GraphQLObjectType>()
    private val entityCache = HashMap<EntityType<*>, GraphQLObjectType>()
    private val attributeMappers = ArrayList<AttributeMapper>()

    /**
     * @return A freshly built [GraphQLSchema]
     */
    val graphQLSchema: GraphQLSchema
        @Deprecated("Use {@link #build()} instead.\n" +
                "      ")
        get() = super.build()

    val queryType: GraphQLObjectType
        get() {
            val queryType = GraphQLObjectType
                    .newObject()
                    .name("JPA_GraphQL")
                    .description("All encompassing schema for this JPA environment")
            queryType.fields(entityManager.metamodel.entities.filter { this.isNotIgnored(it) }.map { this.getQueryFieldDefinition(it) })
            queryType.fields(entityManager.metamodel.embeddables.filter { this.isNotIgnored(it) }.map { this.getQueryEmbeddedFieldDefinition(it) })

            return queryType.build()
        }

    constructor(entityManager: EntityManager) {
        this.entityManager = entityManager

        populateStandardAttributeMappers()

        super.query(queryType)
    }

    constructor(entityManager: EntityManager, attributeMappers: Collection<AttributeMapper>) {
        this.entityManager = entityManager

        this.attributeMappers.addAll(attributeMappers)
        populateStandardAttributeMappers()

        super.query(queryType)
    }

    private fun populateStandardAttributeMappers() {
        attributeMappers.add(createStandardAttributeMapper(UUID::class.java, JavaScalars.GraphQLUUID))
        attributeMappers.add(createStandardAttributeMapper(Date::class.java, JavaScalars.GraphQLDate))
        attributeMappers.add(createStandardAttributeMapper(LocalDateTime::class.java, JavaScalars.GraphQLLocalDateTime))
        attributeMappers.add(createStandardAttributeMapper(Instant::class.java, JavaScalars.GraphQLInstant))
        attributeMappers.add(createStandardAttributeMapper(LocalDate::class.java, JavaScalars.GraphQLLocalDate))
    }

    private fun createStandardAttributeMapper(assignableClass: Class<*>, type: GraphQLType): AttributeMapper {
        return object : AttributeMapper {
            override fun getBasicAttributeType(javaType: Class<*>): Optional<GraphQLType> {
                return if (assignableClass.isAssignableFrom(javaType)) {
                    Optional.of(type)
                } else {
                    Optional.empty()
                }
            }
        }
    }

    private fun getQueryFieldDefinition(entityType: EntityType<*>): GraphQLFieldDefinition {
        val fields = entityType.attributes
                .filter { isValidInput(it) }
                .filter { isNotIgnored(it) }
                .flatMap { getFieldsFilter(it) }

        val argument = GraphQLArgument.newArgument()
                .name("where")
                .type(GraphQLInputObjectType.newInputObject()
                        .name(entityType.name.capitalize() + "Where")
                        .fields(fields)
                        .build()
                ).build()
        GraphQLEnumType.newEnum()
                .name("Direction")
                .description("Describes the direction (Ascending / Descending) to sort a field.")
                .value("ASC", 0, "Ascending")
                .value("DESC", 1, "Descending")
                .build()

        val pageType = GraphQLObjectType.newObject()
                .name(entityType.name + "Connection")
                .description("'Connection' response wrapper object for " + entityType.name + ".  When pagination or aggregation is requested, this object will be returned with metadata about the query.")
                .field(GraphQLFieldDefinition.newFieldDefinition().name("totalPages").description("Total number of pages calculated on the database for this pageSize.").type(Scalars.GraphQLLong).build())
                .field(GraphQLFieldDefinition.newFieldDefinition().name("totalElements").description("Total number of results on the database for this query.").type(Scalars.GraphQLLong).build())
                .field(GraphQLFieldDefinition.newFieldDefinition().name("content").description("The actual object results").type(GraphQLList(getObjectType(entityType))).build())
                .build()

        return GraphQLFieldDefinition.newFieldDefinition()
                .name(entityType.name)
                .description(getSchemaDocumentation(entityType.javaType))
                .type(pageType)
                .dataFetcher(JpaDataFetcher(entityManager, entityType))
                .argument(listOf(argument, paginationArgument))
                .build()
    }

    private fun getQueryEmbeddedFieldDefinition(embeddableType: EmbeddableType<*>): GraphQLFieldDefinition {
        val embeddedName = embeddableType.javaType.simpleName
        val map = embeddableType.attributes.filter { this.isValidInput(it) }.filter { this.isNotIgnored(it) }.flatMap { this.getArgument(it) }
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(embeddedName)
                .description(getSchemaDocumentation(embeddableType.javaType))
                .type(GraphQLList(getObjectType(embeddableType)))
                .argument(map)
                .build()
    }

    private fun getQueryFieldPageableDefinition(entityType: EntityType<*>): GraphQLFieldDefinition {
        val pageType = GraphQLObjectType.newObject()
                .name(entityType.name + "Connection")
                .description("'Connection' response wrapper object for " + entityType.name + ".  When pagination or aggregation is requested, this object will be returned with metadata about the query.")
                .field(GraphQLFieldDefinition.newFieldDefinition().name("totalPages").description("Total number of pages calculated on the database for this pageSize.").type(Scalars.GraphQLLong).build())
                .field(GraphQLFieldDefinition.newFieldDefinition().name("totalElements").description("Total number of results on the database for this query.").type(Scalars.GraphQLLong).build())
                .field(GraphQLFieldDefinition.newFieldDefinition().name("content").description("The actual object results").type(GraphQLList(getObjectType(entityType))).build())
                .build()

        return GraphQLFieldDefinition.newFieldDefinition()
                .name(entityType.name + "Connection")
                .description("'Connection' request wrapper object for " + entityType.name + ".  Use this object in a query to request things like pagination or aggregation in an argument.  Use the 'content' field to request actual fields ")
                .type(pageType)
                // .dataFetcher(ExtendedJpaDataFetcher(entityManager, entityType))
                .argument(paginationArgument)
                .build()
    }

    private fun getArgumentFilter(attribute: Attribute<*, *>): List<GraphQLArgument> {

        return getAttributeType(attribute)
                .filterIsInstance<GraphQLInputType>()
                .filter { type ->
                    attribute.persistentAttributeType != Attribute.PersistentAttributeType.EMBEDDED ||
                            attribute.persistentAttributeType == Attribute.PersistentAttributeType.EMBEDDED
                            && type is GraphQLScalarType
                }
                .flatMap { type ->
                    OPERATORS.map { operator ->
                        var type = type
                        if (operator == "in" || operator == "bt") {
                            type = GraphQLList.list(type)
                        }
                        GraphQLArgument.newArgument()
                                .name("${attribute.name}_$operator")
                                .type(type)
                                .build()
                    }

                }
    }


    private fun getArgument(attribute: Attribute<*, *>): List<GraphQLArgument> {
        return getAttributeType(attribute)
                .filterIsInstance<GraphQLInputType>()
                .filter { type ->
                    attribute.persistentAttributeType != Attribute.PersistentAttributeType.EMBEDDED ||
                            attribute.persistentAttributeType == Attribute.PersistentAttributeType.EMBEDDED
                            && type is GraphQLScalarType
                }
                .map { type ->
                    GraphQLArgument.newArgument()
                            .name(attribute.name)
                            .type(type)
                            .build()
                }
    }

    private fun getObjectType(entityType: EntityType<*>): GraphQLObjectType {
        if (entityCache.containsKey(entityType))
            return entityCache[entityType]!!

        val answer = GraphQLObjectType.newObject()
                .name(entityType.name)
                .description(getSchemaDocumentation(entityType.javaType))
                .fields(entityType.attributes.filter { this.isNotIgnored(it) }.flatMap { this.getObjectField(it) })
                .build()

        entityCache[entityType] = answer

        return answer
    }

    private fun getObjectType(embeddableType: EmbeddableType<*>): GraphQLObjectType {

        if (embeddableCache.containsKey(embeddableType))
            return embeddableCache[embeddableType]!!

        val embeddableName = embeddableType.javaType.simpleName
        val list = embeddableType.attributes.filter { this.isNotIgnored(it) }.flatMap { this.getObjectField(it) }
        val answer = GraphQLObjectType.newObject()
                .name(embeddableName)
                .description(getSchemaDocumentation(embeddableType.javaType))
                .fields(list)
                .build()

        embeddableCache[embeddableType] = answer

        return answer
    }

    private fun getObjectField(attribute: Attribute<*, *>): List<GraphQLFieldDefinition> {
        return getAttributeType(attribute)
                .filterIsInstance<GraphQLOutputType>()
                .map { type ->
                    val arguments = ArrayList<GraphQLArgument>()
                    arguments.add(GraphQLArgument.newArgument().name("orderBy").type(orderByDirectionEnum).build())            // Always add the orderBy argument
                    // Get the fields that can be queried on (i.e. Simple Types, no Sub-Objects)
                    if (attribute is SingularAttribute<*, *> && attribute.getPersistentAttributeType() != Attribute.PersistentAttributeType.BASIC) {
                        val foreignType = attribute.type as ManagedType<*>

                        findBasicAttributes(foreignType.attributes).forEach {
                            arguments.add(GraphQLArgument.newArgument()
                                    .name(it.name)
                                    .type(getAttributeType(it).first() as GraphQLInputType)
                                    .build())
                        }
                    }

                    GraphQLFieldDefinition.newFieldDefinition()
                            .name(attribute.name)
                            .description(getSchemaDocumentation(attribute.javaMember))
                            .type(type)
                            //  .argument(arguments)
                            .build()
                }

    }

    private fun findBasicAttributes(attributes: Collection<Attribute<*, *>>): List<Attribute<*, *>> {
        return attributes.filter { this.isNotIgnored(it) }.filter { it.persistentAttributeType == Attribute.PersistentAttributeType.BASIC }
    }

    private fun getBasicAttributeType(javaType: Class<*>): GraphQLType {
        // First check our 'standard' and 'customized' Attribute Mappers.  Use them if possible
        val customMapper = attributeMappers.stream()
                .filter { it.getBasicAttributeType(javaType).isPresent }
                .findFirst()

        if (customMapper.isPresent)
            return customMapper.get().getBasicAttributeType(javaType).get()
        else if (String::class.java.isAssignableFrom(javaType))
            return Scalars.GraphQLString
        else if (Int::class.java.isAssignableFrom(javaType) || Int::class.javaPrimitiveType!!.isAssignableFrom(javaType))
            return Scalars.GraphQLInt
        else if (Short::class.java.isAssignableFrom(javaType) || Short::class.javaPrimitiveType!!.isAssignableFrom(javaType))
            return Scalars.GraphQLShort
        else if (Float::class.javaObjectType.isAssignableFrom(javaType) || Float::class.java.isAssignableFrom(javaType) || Float::class.javaPrimitiveType!!.isAssignableFrom(javaType)
                || Double::class.java.isAssignableFrom(javaType) || Double::class.javaPrimitiveType!!.isAssignableFrom(javaType))
            return Scalars.GraphQLFloat
        else if (Long::class.javaObjectType.isAssignableFrom(javaType) || Long::class.java.isAssignableFrom(javaType) || Long::class.javaPrimitiveType!!.isAssignableFrom(javaType))
            return Scalars.GraphQLLong
        else if (Boolean::class.java.isAssignableFrom(javaType) || Boolean::class.javaPrimitiveType!!.isAssignableFrom(javaType))
            return Scalars.GraphQLBoolean
        else if (javaType.isEnum) {
            return getTypeFromJavaType(javaType)
        } else if (BigDecimal::class.java.isAssignableFrom(javaType)) {
            return Scalars.GraphQLBigDecimal
        }

        throw UnsupportedOperationException(
                "Class could not be mapped to GraphQL: '" + javaType.typeName + "'")
    }

    private fun getAttributeType(attribute: Attribute<*, *>): List<GraphQLType> {
        val declaringType = attribute.declaringType.javaType.name // fully qualified name of the entity class
        val declaringMember = attribute.javaMember.name // field name in the entity class

        if (attribute.persistentAttributeType == Attribute.PersistentAttributeType.BASIC) {
            try {
                return listOf(getBasicAttributeType(attribute.javaType))
            } catch (e: UnsupportedOperationException) {
                throw e
                //fall through to the exception below
                //which is more useful because it also contains the declaring member
            }

        } else if (attribute.persistentAttributeType == Attribute.PersistentAttributeType.ONE_TO_MANY || attribute.persistentAttributeType == Attribute.PersistentAttributeType.MANY_TO_MANY) {
            val foreignType = (attribute as PluralAttribute<*, *, *>).elementType as EntityType<*>
            return listOf(GraphQLList(GraphQLTypeReference(foreignType.name)))
        } else if (attribute.persistentAttributeType == Attribute.PersistentAttributeType.MANY_TO_ONE || attribute.persistentAttributeType == Attribute.PersistentAttributeType.ONE_TO_ONE) {
            val foreignType = (attribute as SingularAttribute<*, *>).type as EntityType<*>
            return listOf(GraphQLTypeReference(foreignType.name))
        } else if (attribute.persistentAttributeType == Attribute.PersistentAttributeType.ELEMENT_COLLECTION) {
            val foreignType = (attribute as PluralAttribute<*, *, *>).elementType
            return listOf(GraphQLList(getTypeFromJavaType(foreignType.javaType)))
        } else if (attribute.persistentAttributeType == Attribute.PersistentAttributeType.EMBEDDED) {
            val embeddableType = (attribute as SingularAttribute<*, *>).type as EmbeddableType<*>
            return listOf(GraphQLTypeReference(embeddableType.javaType.simpleName))
        }



        throw UnsupportedOperationException(
                "Attribute could not be mapped to GraphQL: field '$declaringMember' of entity class '$declaringType' ${attribute.persistentAttributeType}")
    }

    private fun isValidInput(attribute: Attribute<*, *>): Boolean {
        return attribute.persistentAttributeType == Attribute.PersistentAttributeType.BASIC ||
                attribute.persistentAttributeType == Attribute.PersistentAttributeType.ELEMENT_COLLECTION ||
                attribute.persistentAttributeType == Attribute.PersistentAttributeType.EMBEDDED
    }

    private fun getSchemaDocumentation(member: Member): String? {
        return if (member is AnnotatedElement) {
            getSchemaDocumentation(member as AnnotatedElement)
        } else null

    }

    private fun getSchemaDocumentation(annotatedElement: AnnotatedElement?): String? {
        if (annotatedElement != null) {
            val schemaDocumentation = annotatedElement.getAnnotation(SchemaDocumentation::class.java)
            return schemaDocumentation?.value
        }

        return null
    }

    private fun isNotIgnored(attribute: Attribute<*, *>): Boolean {
        return isNotIgnored(attribute.javaMember) && isNotIgnored(attribute.javaType)
    }

    private fun isNotIgnored(embeddableType: EmbeddableType<*>): Boolean {
        return isNotIgnored(embeddableType.javaType)
    }

    private fun isNotIgnored(entityType: EntityType<*>): Boolean {
        return isNotIgnored(entityType.javaType)
    }

    private fun isNotIgnored(member: Member): Boolean {
        return member is AnnotatedElement && isNotIgnored(member as AnnotatedElement)
    }

    private fun isNotIgnored(annotatedElement: AnnotatedElement?): Boolean {
        if (annotatedElement != null) {
            val schemaDocumentation = annotatedElement.getAnnotation(GraphQLIgnore::class.java)
            return schemaDocumentation == null
        }

        return false
    }

    private fun getTypeFromJavaType(clazz: Class<*>): GraphQLType {
        if (clazz.isEnum) {
            if (classCache.containsKey(clazz))
                return classCache[clazz]!!

            val enumBuilder = GraphQLEnumType.newEnum().name(clazz.simpleName)
            var ordinal = 0
            for (enumValue in (clazz as Class<Enum<*>>).enumConstants)
                enumBuilder.value(enumValue.name, ordinal++)

            val answer = enumBuilder.build()
            setIdentityCoercing(answer)

            classCache[clazz] = answer

            return answer
        }

        return getBasicAttributeType(clazz)
    }

    /**
     * A bit of a hack, since JPA will deserialize our Enum's for us...we don't want GraphQL doing it.
     * @param type
     */
    private fun setIdentityCoercing(type: GraphQLType) {
        val coercing = type.javaClass.getDeclaredField("coercing")
        coercing.isAccessible = true
        coercing.set(type, IdentityCoercing())
    }

    private fun getFieldsFilter(attribute: Attribute<*, *>): List<GraphQLInputObjectField> {
        return getAttributeType(attribute)
                .filterIsInstance<GraphQLInputType>()
                .filter { type ->
                    attribute.persistentAttributeType != Attribute.PersistentAttributeType.EMBEDDED ||
                            attribute.persistentAttributeType == Attribute.PersistentAttributeType.EMBEDDED
                            && type is GraphQLScalarType
                }.flatMap {
                    OPERATORS.map { operator ->
                        var type = it
                        if (operator == "in" || operator == "bt") {
                            type = GraphQLList.list(type)
                        }
                        GraphQLInputObjectField.newInputObjectField()
                                .name("${attribute.name}_$operator")
                                .type(type).build()
                    }

                }
    }

    companion object {
        private val OPERATORS = listOf("eq", "in", "like", "gte", "gt", "lt", "lte", "bt")

        private val paginationArgument = GraphQLArgument.newArgument()
                .name("pageRequest")
                .type(GraphQLInputObjectType.newInputObject()
                        .name("Pageable")
                        .description("Query object for Pagination Requests, specifying the requested page, and that page's size.\n\nNOTE: 'page' parameter is 1-indexed, NOT 0-indexed.\n\nExample: paginationRequest { page: 1, size: 20 }")
                        .field(GraphQLInputObjectField.newInputObjectField().name("page")
                                .description("Which page should be returned, starting with 1 (1-indexed)")
                                .type(Scalars.GraphQLInt).build())
                        .field(GraphQLInputObjectField.newInputObjectField().name("size")
                                .description("How many results should this page contain")
                                .type(Scalars.GraphQLInt).build())
                        .build()
                ).build()

        private val orderByDirectionEnum = GraphQLEnumType.newEnum()
                .name("Direction")
                .description("Describes the direction (Ascending / Descending) to sort a field.")
                .value("ASC", 0, "Ascending")
                .value("DESC", 1, "Descending")
                .build()
    }
}
