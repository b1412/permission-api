package com.github.b1412.permission.controller

import com.github.b1412.api.service.SecurityFilter
import com.github.b1412.cache.CacheClient
import com.github.b1412.permission.config.WebConfig
import com.github.b1412.permission.dao.PermissionDao
import com.github.b1412.permission.entity.Branch
import com.github.b1412.permission.entity.Permission
import com.github.b1412.permission.entity.Role
import com.github.b1412.permission.entity.User
import com.github.b1412.error.GlobalExceptionHandler
import com.github.b1412.json.JsonReturnHandler
import com.github.b1412.permission.service.UserService
import com.github.b1412.security.*
import com.github.b1412.security.config.SecurityConfig
import com.github.b1412.security.custom.CustomUserDetailsServiceImpl
import com.github.b1412.security.handlers.AuthenticationFailureHandler
import com.github.b1412.security.handlers.AuthenticationSuccessHandler
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.persistence.EntityManager

@WebMvcTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = [
    PermissionController::class,
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
    GlobalExceptionHandler::class,
    BCryptPasswordEncoder::class,
    TokenHelper::class]
)
class AciControllerTest {

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
        // given
        val role1 = Role(name = "admin").apply { this.id = 1 }
        val role2 = Role(name = "manager").apply { this.id = 2 }
        val user1 = User(login = "login1", address = "address1", email = "email1", notes = "notes1", active = true, role = role1).apply { this.id = 1 }
        val user2 = User(login = "login2", address = "address2", email = "email2", notes = "notes2", active = false, role = role2).apply { this.id = 2 }
        val branchA = Branch(name = "branchA", active = true, users = mutableListOf(user1)).apply { this.id = 1 }
        val branchB = Branch(name = "branchB", active = false, users = mutableListOf(user2)).apply { this.id = 2 }
        user1.branch = branchA
        user2.branch = branchB
        val mockedBranches = listOf(branchA, branchB)
        //  every { permissionService.searchBySecurity(any(), any(), any()) } returns mockedBranches
    }

    @Test
    fun `will not return embedded fields by default`() {
        // given
        every { tokenHelper.getToken(any()) } returns "token"
        every { tokenHelper.getUsernameFromToken("token") } returns Result.success("leon@@4")
        every { userService.getUserWithPermissions("leon", "4") } returns User(username = "leon", clientId = "4")
        every { cacheClient.get<List<Permission>>("permissions", any()) } returns listOf(Permission())

        // when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/v1/permission/filter?method=GET&uri=/v1/branch")
                .header("Authorization", "Bearer: xxx"))
        // then
        resultActions
                .andExpect(status().is3xxRedirection)

    }

}