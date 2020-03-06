package com.github.b1412.cannon


import com.github.b1412.cannon.config.CustomDateTimeProvider
import com.github.b1412.cannon.config.JpaConfig
import com.github.b1412.cannon.controller.GraphQlController
import com.github.b1412.cannon.entity.Role
import com.github.b1412.cannon.entity.User
import com.github.b1412.cannon.extenstions.print
import com.github.b1412.cannon.graphql.GraphQLExecutor
import com.github.b1412.cannon.graphql.GraphQLSchemaBuilder
import org.json.JSONObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.data.auditing.config.AuditingConfiguration
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import javax.persistence.EntityManager


@AutoConfigureWebMvc
@AutoConfigureMockMvc
@DataJpaTest(
        includeFilters = [ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = [AuditingConfiguration::class, JpaConfig::class,
                    CustomDateTimeProvider::class, DatabaseCleanupService::class,
                    JpaRepositoriesAutoConfiguration::class,
                    GraphQlController::class, GraphQLExecutor::class, GraphQLSchemaBuilder::class]
        )]
)

@ActiveProfiles("test")
class GraphQLIntegTest {

    private var mockMvc: MockMvc? = null

    @Autowired
    var graphQlController: GraphQlController? = null

    @Autowired
    var entityManager: EntityManager? = null

    @BeforeEach
    fun setup() {
        this.mockMvc = standaloneSetup(this.graphQlController).build()

        repeat(10) {
            val role = Role(name = "role$it")
            entityManager!!.persist(role)
            val user = User(address = "address$it", email = "foo$it", notes = "notes$it", login = "foo$it", role = role)
            entityManager!!.persist(user)
        }
        entityManager!!.flush()
    }

    @Test
    fun `users query`() {
        // Given
        val query = """
{
  User {
    totalPages
    totalElements
    content {
      id
      login
      email
      createdAt
      updatedAt
    }
  }
}
        """
        // When
        val postResult = performGraphQlPost(query)
        // Then
        postResult.andExpect(status().isOk)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.errors").isEmpty)
                .andExpect(jsonPath("$.data.User.totalPages").value(1))
                .andExpect(jsonPath("$.data.User.content.size()").value(10))
                .andExpect(jsonPath("$.data.User.content[0].id").value(1))
                .andExpect(jsonPath("$.data.User.content[0].email").value("foo0"))
                .andExpect(jsonPath("$.data.User.content[0].createdAt").exists())
                .andExpect(jsonPath("$.data.User.content[0].updatedAt").exists())
    }

    @Test
    fun `users query embedded`() {
        // Given
        val query = """
{
  User {
    totalPages
    totalElements
    content {
      id
      login
      email
      createdAt
      updatedAt
      role{
        id
      }
    }
  }
}
        """
        // When
        val postResult = performGraphQlPost(query)
        // Then
        postResult.andExpect(status().isOk)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.errors").isEmpty)
                .andExpect(jsonPath("$.data.User.totalPages").value(1))
                .andExpect(jsonPath("$.data.User.content.size()").value(10))
                .andExpect(jsonPath("$.data.User.content[0].id").value(1))
                .andExpect(jsonPath("$.data.User.content[0].email").value("foo0"))
                .andExpect(jsonPath("$.data.User.content[0].role.id").value(1))
    }

    @Test
    fun `users query where`() {
        // Given
        val query = """
{
  User (where: {id_eq: 1}){
    totalPages
    totalElements
    content {
      id
      login
      email
      createdAt
      updatedAt
    }
  }
}
        """
        // When
        val postResult = performGraphQlPost(query)
        // Then
        postResult.andExpect(status().isOk)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.errors").isEmpty)
                .andExpect(jsonPath("$.data.User.totalPages").value(1))
                .andExpect(jsonPath("$.data.User.content.size()").value(1))
                .andExpect(jsonPath("$.data.User.content[0].id").value(1))
                .andExpect(jsonPath("$.data.User.content[0].email").value("foo0"))
    }


    @Test
    fun `users query where 2`() {
        // Given
        val query = """
{
  User (where: {login_like: "f", notes_like: "note" }){
    totalPages
    totalElements
    content {
      id
      login
      email
      createdAt
      updatedAt
    }
  }
}
        """
        // When
        val postResult = performGraphQlPost(query)
        // Then
        postResult.andExpect(status().isOk)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.errors").isEmpty)
                .andExpect(jsonPath("$.data.User.totalPages").value(1))
                .andExpect(jsonPath("$.data.User.content.size()").value(10))
                .andExpect(jsonPath("$.data.User.content[0].id").value(1))
                .andExpect(jsonPath("$.data.User.content[0].email").value("foo0"))
    }


    @Test
    fun `users query page`() {
        // Given
        val query = """
{
  User(pageRequest: {size: 2, page: 2}) {
    totalPages
    totalElements
    content {
      id
      login
      email
      createdAt
      updatedAt
    }
  }
}
        """
        // When
        val postResult = performGraphQlPost(query)
        // Then
        postResult.andExpect(status().isOk)
                .andDo { print() }
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.errors").isEmpty)
                .andExpect(jsonPath("$.data.User.totalPages").value(5))
                .andExpect(jsonPath("$.data.User.totalElements").value(10))
                .andExpect(jsonPath("$.data.User.content.size()").value(2))
                .andExpect(jsonPath("$.data.User.content[0].id").value(3))
                .andExpect(jsonPath("$.data.User.content[0].email").value("foo2"))
    }

    @Autowired
    private lateinit var truncateDatabaseService: DatabaseCleanupService


    @AfterEach
    fun cleanupAfterEach() {
        truncateDatabaseService.truncate()
    }

    private fun performGraphQlPost(query: String, variables: Map<*, *>? = null): ResultActions {
        return mockMvc!!.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(generateRequest(query, variables))
        )
    }

    private fun generateRequest(query: String, variables: Map<*, *>?): String {
        val jsonObject = JSONObject()
        jsonObject.put("query", query)
        if (variables != null) {
            jsonObject.put("variables", mapOf("input" to variables))
        }
        return jsonObject.toString()
    }
}
