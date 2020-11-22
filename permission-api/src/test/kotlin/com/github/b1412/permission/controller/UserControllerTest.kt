package com.github.b1412.permission.controller


import com.github.b1412.permission.config.WebConfig
import com.github.b1412.permission.entity.User
import com.github.b1412.error.GlobalExceptionHandler
import com.github.b1412.json.JsonReturnHandler
import com.github.b1412.permission.service.BranchService
import com.github.b1412.permission.service.RoleService
import com.github.b1412.permission.service.UserService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.PageImpl
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@ContextConfiguration(classes = [
    UserController::class,
    WebConfig::class,
    JsonReturnHandler::class,
    GlobalExceptionHandler::class,
    BCryptPasswordEncoder::class
])
@WebMvcTest
class UserControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var userService: UserService

    @MockkBean
    lateinit var roleService: RoleService

    @MockkBean
    lateinit var branchService: BranchService

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
        val mockedUsers = PageImpl(listOf(userA, userB))
        every { userService.searchBySecurity(any(), any(), any(), any()) } returns mockedUsers
        // when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/v1/user"))
        // then
        resultActions
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content[0].id", Matchers.`is`(1)))
                .andExpect(jsonPath("$.content[0].login", Matchers.`is`("login of user A")))
                .andExpect(jsonPath("$.content[0].address", Matchers.`is`("address of user A")))
                .andExpect(jsonPath("$.content[0].email", Matchers.`is`("email of user A")))
                .andExpect(jsonPath("$.content[0].notes", Matchers.`is`("notes of user A")))
                .andExpect(jsonPath("$.content[1].id", Matchers.`is`(2)))
                .andExpect(jsonPath("$.content[1].login", Matchers.`is`("login of user B")))
                .andExpect(jsonPath("$.content[1].address", Matchers.`is`("address of user B")))
                .andExpect(jsonPath("$.content[1].email", Matchers.`is`("email of user B")))
                .andExpect(jsonPath("$.content[1].notes", Matchers.`is`("notes of user B")))
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
        every { userService.findByIdOrNull(1) } returns user
        // when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/v1/user/1"))
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
        every { userService.findByIdOrNull(1) } returns null
        //when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/v1/user/1"))
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
        every { userService.save(any<User>()) } returns user
        every { userService.syncFromDb(any<User>()) } just Runs
        val body = """
  {
    "password":"password",
    "confirmPassword":"password",
    "login": "login of user",
    "address": "address of user",
    "email": "email of user",
    "notes": "notes of user"
  }
        """
        // when
        val resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/v1/user")
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
        every { userService.findByIdOrNull(1) } returns null
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
                        .put("/user/1")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        )
        //then
        resultActions.andExpect(status().isNotFound)
    }

    @Test
    fun `update user return 204 when id exists`() {
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

        every { userService.findByIdOrNull(1) } returns persistedUser
        every { userService.syncFromDb(any()) } just runs
        every { userService.save(any<User>()) } returns updatedUser
        val body = """
  {
    "login": "new login of user",
    "address": "new address of user",
    "password": "new password",
    "confirmPassword": "new password",
    "email": "new email of user",
    "notes": "new notes of user"
  }
        """
        // when
        val resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/v1/user/1")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        )
        // then
        resultActions
                .andExpect(status().isNoContent)
    }

    @Test
    fun `return 204 when delete an existing user`() {
        // given
        every { userService.deleteById(1) } just Runs
        //when
        val resultActions =
                mockMvc.perform(MockMvcRequestBuilders.delete("/v1/user/1"))
        // then
        resultActions.andExpect(status().isNoContent)
    }


    @Test
    fun `return 404 when delete an non-existing user`() {
        //given
        every { userService.deleteById(1) } throws EmptyResultDataAccessException(1)
        //when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/v1/user/1"))
        //then
        resultActions.andExpect(status().isNotFound)
    }
}
