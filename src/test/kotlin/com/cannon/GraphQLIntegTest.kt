package com.cannon

import com.cannon.controller.GraphQlController
import org.json.JSONObject
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import org.springframework.web.context.WebApplicationContext


@SpringBootTest
@ActiveProfiles("test")
class GraphQLIntegTest {

    private var mockMvc: MockMvc? = null

    @Autowired
    protected var wac: WebApplicationContext? = null
    @Autowired
    var graphQlController: GraphQlController? = null

    @BeforeAll
    fun setup() {
        this.mockMvc = standaloneSetup(this.graphQlController).build()
    }

    @Test
    fun `users query`() {
        // Given
        val query = """
            {
  User(where: {email_like: "foo", login_like: "f"}, pageRequest: {size: 5, page: 1}) {
    totalPages
    totalElements
    content {
      id
      login
      email
      role {
        id
        name
      }
    }
  }
}
        """

        // When
        val postResult = performGraphQlPost(query)

        // Then
        postResult.andExpect(status().isOk)
                .andExpect(jsonPath("$.errors").isEmpty)
                .andExpect(jsonPath("$.data.User.totalPages").value(2))
                .andExpect(jsonPath("$.data.User.content.size()").value(5))
                .andExpect(jsonPath("$.data.User.content[0].id").value(1))

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
