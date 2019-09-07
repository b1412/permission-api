package com.cannon.controller

import com.cannon.config.WebConfig
import com.cannon.dao.UserDao
import com.cannon.entity.User
import com.cannon.json.JsonReturnHandler
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@ContextConfiguration(classes = [UserController::class, WebConfig::class, JsonReturnHandler::class])
@WebMvcTest
class UserControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var userDao: UserDao

    @BeforeAll
    fun setup() {
        println(">> Setup")
    }

    @Test
    fun `return a list of users with 200`() {
        // given
        val userA = User(
                login = "login of user A",
                address = "address of user A",
                email = "email of user A",
                notes = "notes of user A"
        ).apply { this.id = 1 }
        val userB = User(
                login = "login of user B",
                address = "address of user B",
                email = "email of user B",
                notes = "notes of user B"
        ).apply { this.id = 2 }
        val mockedUsers = listOf(userA, userB)
        every { userDao.searchByFilter(any()) } returns mockedUsers
        // when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/user"))
        // then
        resultActions
                .andExpect(status().isOk)
                .andExpect(jsonPath("$[0].id", Matchers.`is`(1)))
                .andExpect(jsonPath("$[0].login", Matchers.`is`("login of user A")))
                .andExpect(jsonPath("$[0].address", Matchers.`is`("address of user A")))
                .andExpect(jsonPath("$[0].email", Matchers.`is`("email of user A")))
                .andExpect(jsonPath("$[0].notes", Matchers.`is`("notes of user A")))
                .andExpect(jsonPath("$[1].id", Matchers.`is`(2)))
                .andExpect(jsonPath("$[1].login", Matchers.`is`("login of user B")))
                .andExpect(jsonPath("$[1].address", Matchers.`is`("address of user B")))
                .andExpect(jsonPath("$[1].email", Matchers.`is`("email of user B")))
                .andExpect(jsonPath("$[1].notes", Matchers.`is`("notes of user B")))
    }

    @Test
    fun `return a user with 200 when id exists`() {
        // given
        val user = User(
                login = "login of user",
                address = "address of user",
                email = "email of user",
                notes = "notes of user"
        ).apply { this.id = 1 }
        every { userDao.findByIdOrNull(1) } returns user
        // when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/user/1"))
        // then
        resultActions
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id", Matchers.`is`(1)))
                .andExpect(jsonPath("$.login", Matchers.`is`("login of user")))
                .andExpect(jsonPath("$.address", Matchers.`is`("address of user")))
                .andExpect(jsonPath("$.email", Matchers.`is`("email of user")))
                .andExpect(jsonPath("$.notes", Matchers.`is`("notes of user")))
    }

    @Test
    fun `get user return empty body with 404 when id doesn't exist`() {
        //given
        every { userDao.findByIdOrNull(1) } returns null
        //when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/user/1"))
        //then
        resultActions.andExpect(status().isNotFound)
    }

    @Test
    fun `save a new user`() {
        // given
        val user = User(
                login = "login of user",
                address = "address of user",
                email = "email of user",
                notes = "notes of user"
        ).apply { this.id = 1 }
        every { userDao.save(any<User>()) } returns user
        val body = """
  {
    "login": "login of user",
    "address": "address of user",
    "email": "email of user",
    "notes": "notes of user"
  }
        """
        // when
        val resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/user")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        )
        // then
        resultActions
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id", Matchers.`is`(1)))
                .andExpect(jsonPath("$.login", Matchers.`is`("login of user")))
                .andExpect(jsonPath("$.address", Matchers.`is`("address of user")))
                .andExpect(jsonPath("$.email", Matchers.`is`("email of user")))
                .andExpect(jsonPath("$.notes", Matchers.`is`("notes of user")))
    }

    @Test
    fun `update user return empty body with 404 when id doesn't exist`() {
        //given
        every { userDao.findByIdOrNull(1) } returns null
        //when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/user/1"))
        //then
        resultActions.andExpect(status().isNotFound)
    }

    @Test
    fun `update user return updated blog with 200 when id exists`() {
        // given
        val persistedUser = User(
                login = "login of user",
                address = "address of user",
                email = "email of user",
                notes = "notes of user"
        ).apply { this.id = 1 }
        val updatedUser = User(
                login = "new login of user",
                address = "new address of user",
                email = "new email of user",
                notes = "new notes of user"
        ).apply { this.id = 1 }

        every { userDao.findByIdOrNull(1) } returns persistedUser
        every { userDao.save(any<User>()) } returns updatedUser
        val body = """
  {
    "login": "new login of user",
    "address": "new address of user",
    "email": "new email of user",
    "notes": "new notes of user"
  }
        """
        // when
        val resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/user/1")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        )
        // then
        resultActions
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id", Matchers.`is`(1)))
                .andExpect(jsonPath("$.login", Matchers.`is`("new login of user")))
                .andExpect(jsonPath("$.address", Matchers.`is`("new address of user")))
                .andExpect(jsonPath("$.email", Matchers.`is`("new email of user")))
                .andExpect(jsonPath("$.notes", Matchers.`is`("new notes of user")))
    }

    @Test
    fun `return 204 when delete an existing user`() {
        // given
        every { userDao.deleteById(1) } just Runs
        //when
        val resultActions =
                mockMvc.perform(MockMvcRequestBuilders.delete("/user/1"))
        // then
        resultActions.andExpect(status().isNoContent)
    }


    @Test
    fun `return 404 when delete an non-existing user`() {
        //given
        every { userDao.deleteById(1) } throws EmptyResultDataAccessException(1)
        //when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/user/1"))
        //then
        resultActions.andExpect(status().isNotFound)
    }


    @AfterAll
    fun teardown() {
        println(">> Tear down")
    }

}