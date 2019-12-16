package com.github.b1412.security.handlers


import com.fasterxml.jackson.databind.ObjectMapper
import com.github.b1412.cannon.cache.CacheClient
import com.github.b1412.cannon.entity.User
import com.github.b1412.cannon.service.UserService
import com.github.b1412.security.ApplicationProperties
import com.github.b1412.security.TokenHelper

import com.github.b1412.security.UserTokenState

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@EnableConfigurationProperties(value = [(ApplicationProperties::class)])
@Component
class AuthenticationSuccessHandler(
        @Value("\${spring.application.name}")
        var application: String,
        @Autowired
        var tokenHelper: TokenHelper,
        @Autowired
        var objectMapper: ObjectMapper,

        @Autowired
        var applicationProperties: ApplicationProperties,

        @Autowired
        var userService: UserService,

        @Autowired
        var cacheClient: CacheClient

) : SimpleUrlAuthenticationSuccessHandler() {


    @Transactional
    override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse,
                                         authentication: Authentication) {
        clearAuthenticationAttributes(request)
        val user = authentication.principal as User

        cacheClient.set("$application-${user.username}-${user.clientId}".toLowerCase(), userService.getUserWithPermissions(user.username!!, user.clientId!!))
        val jws = tokenHelper.generateToken(user.username!!, user.clientId!!)
        val jwt = applicationProperties.jwt
        val userTokenState = UserTokenState(
                access_token = jws,
                //expires_in = user.expiresIn.orElse(jwt.expiresIn),
                expires_in = jwt.expiresIn
                //type = user.userType.toOption().map { it.name }.getOrElse { "" }
        )


        val jwtResponse = objectMapper.writeValueAsString(userTokenState)
        response.contentType = "application/json"
        response.writer.write(jwtResponse)
    }
}
