package com.cannon

import com.cannon.bean.User
import com.cannon.controller.GraphQlController
import org.json.JSONObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager


@SpringBootTest
@ActiveProfiles("test")
@Transactional
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
            val user = User(address = "address$it", email = "foo$it", notes = "notes$it", login = "foo$it")
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
