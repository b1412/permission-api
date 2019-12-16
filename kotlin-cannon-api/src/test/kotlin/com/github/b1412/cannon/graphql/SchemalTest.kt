package com.github.b1412.cannon.graphql


import com.github.b1412.cannon.config.JpaConfig
import com.github.b1412.cannon.entity.Branch
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import javax.persistence.EntityManager

@DataJpaTest(
        includeFilters = [ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = [GraphQLSchemaBuilder::class, JpaConfig::class]
        )]
)
class SchemalTest {

    @Autowired
    lateinit var schemaBuilder: GraphQLSchemaBuilder

    @Autowired
    lateinit var entityManager: EntityManager

    @Test
    fun `schemaBuilder field definition`() {
        schemaBuilder.getQueryType()

        val entityType = entityManager.metamodel.entities.first { it.javaType == Branch::class.java }
        println(entityType)
        val fieldDefinition = schemaBuilder.getQueryFieldDefinition(entityType)
        println(fieldDefinition.arguments)
    }

}