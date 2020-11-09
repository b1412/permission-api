package com.github.b1412.permission.controller

import com.github.b1412.api.service.SecurityFilter
import com.github.b1412.cache.CacheClient
import com.github.b1412.permission.config.WebConfig
import com.github.b1412.permission.dao.PermissionDao
import com.github.b1412.permission.entity.Permission
import com.github.b1412.permission.entity.User
import com.github.b1412.error.GlobalExceptionHandler
import com.github.b1412.permission.service.UserService
import com.github.b1412.json.JsonReturnHandler
import com.github.b1412.security.*
import com.github.b1412.security.config.SecurityConfig
import com.github.b1412.security.custom.CustomUserDetailsServiceImpl
import com.github.b1412.security.handlers.AuthenticationFailureHandler
import com.github.b1412.security.handlers.AuthenticationSuccessHandler
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.persistence.EntityManager

@WebMvcTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = [
    UserController::class,
    GlobalExceptionHandler::class,
    SecurityConfig::class,
    TokenAuthenticationFilter::class,
    CustomUserDetailsServiceImpl::class,
    AuthenticationSuccessHandler::class,
    AuthenticationFailureHandler::class,
    MyFilterSecurityInterceptor::class,
    MyInvocationSecurityMetadataSourceService::class,
    MyAccessDecisionManager::class,
    WebConfig::class,
    SecurityFilter::class,
    JsonReturnHandler::class,
    BCryptPasswordEncoder::class]
)
class AuthControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var userService: UserService

    @MockkBean
    lateinit var cacheClient: CacheClient

    @MockkBean
    lateinit var tokenHelper: TokenHelper

    @MockkBean
    lateinit var permissionDao: PermissionDao

    @MockkBean
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var passwordEncoder: BCryptPasswordEncoder

    @BeforeEach
    fun setup() {
        val rawPassword = "1qazxsw2"
        val clientId = "4"
        every { userService.getUserWithPermissions("test", clientId) } returns User(
                username = "test", clientId = clientId, password = passwordEncoder.encode(rawPassword))
        every { cacheClient.get<List<Permission>>("permissions", any()) } returns listOf(Permission())
        every { cacheClient.set("cannon-test-${clientId}", any()) } returns Unit
        every { tokenHelper.generateToken("test", clientId) } returns "token"
    }

    @Test
    fun `return 401 when bad credentials`() {
        // when
        val resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"username":"test","password":"wrong password"}
                        """))

        // then
        resultActions.andExpect(status().isUnauthorized)

    }

    @Test
    fun `return 400 when password not equal`() {
        // when
        val resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"username":"test","password":"1qazxsw2"}
                        """))

        // then
        resultActions.andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.expires_in", Matchers.`is`(864000)))
    }


}