package com.github.b1412.security

import arrow.core.None
import arrow.core.Some
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.b1412.error.ErrorDTO
import com.github.b1412.permission.service.UserService
import com.github.b1412.security.custom.CustomUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@EnableConfigurationProperties(value = [PermissionProperties::class])
@Component
class TokenAuthenticationFilter(
    @Autowired
    val tokenHelper: TokenHelper,
    @Autowired
    val objectMapper: ObjectMapper,
    @Autowired
    val permissionProperties: PermissionProperties,
    @Autowired
    val userDetailsService: CustomUserDetailsService,
    @Autowired
    val userService: UserService

) : OncePerRequestFilter() {

    public override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        when {
            skipPathRequest(request, permissionProperties.jwt.anonymousUrls) -> {
                SecurityContextHolder.getContext().authentication = AnonAuthentication()
                chain.doFilter(request, response)
            }
            else -> {
                val authToken = tokenHelper.getToken(request)
                if (authToken.isNullOrBlank()) {
                    badToken(request, response, "token is empty")
                }
                val usernameResult = kotlin.runCatching { tokenHelper.getUsernameFromToken(authToken!!) }
                if (usernameResult.isSuccess) {
                    val orNull = usernameResult.getOrNull()!!
                    val (username, clientId) = orNull.split("@@")
                    val userDetails = userDetailsService.loadUserByUsernameAndClientId(username, clientId)
                    val authentication = TokenBasedAuthentication(userDetails)
                    authentication.token = authToken
                    SecurityContextHolder.getContext().authentication = authentication
                    chain.doFilter(request, response)
                } else {
                    val clientId = request.getParameter("clientId")
                    if (clientId == null) {
                        loginExpired(request, response, usernameResult.exceptionOrNull()!!.message!!)
                    } else {
                        when (val option = userService.loadAuthenticationByClientId(clientId)) {
                            is Some -> {
                                SecurityContextHolder.getContext().authentication = option.t
                                chain.doFilter(request, response)
                            }
                            None -> {
                                loginExpired(request, response, usernameResult.exceptionOrNull()!!.message!!)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loginExpired(request: HttpServletRequest, response: HttpServletResponse, message: String) {
        logger.warn(request.method + request.requestURI)
        val msg = objectMapper.writeValueAsString(ErrorDTO(message = message))
        response.status = HttpStatus.FORBIDDEN.value()
        response.writer.write(msg)
    }

    private fun badToken(request: HttpServletRequest, response: HttpServletResponse, message: String) {
        logger.warn(request.method + request.requestURI)
        val msg = objectMapper.writeValueAsString(ErrorDTO(message = message))
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.writer.write(msg)
    }

    private fun skipPathRequest(request: HttpServletRequest, pathsToSkip: List<String>): Boolean {
        val m = pathsToSkip.map { AntPathRequestMatcher(it) }
        return OrRequestMatcher(m).matches(request)
    }
}