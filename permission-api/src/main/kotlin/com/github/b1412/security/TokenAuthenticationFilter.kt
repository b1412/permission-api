package com.github.b1412.security


import arrow.core.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.b1412.permission.service.UserService
import com.github.b1412.error.ErrorDTO
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


@EnableConfigurationProperties(value = [ApplicationProperties::class])
@Component
class TokenAuthenticationFilter(
        @Autowired
        val tokenHelper: TokenHelper,
        @Autowired
        val objectMapper: ObjectMapper,
        @Autowired
        val applicationProperties: ApplicationProperties,
        @Autowired
        val userDetailsService: CustomUserDetailsService,
        @Autowired
        val userService: UserService

) : OncePerRequestFilter() {

    public override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val wrapRequest = AuthenticationRequestWrapper(request)
        val pathsToSkip = applicationProperties.jwt.anonymousUrls.toOption()
                .map { it.split(",") }
                .map { it.toList() }
                .getOrElse { emptyList() }

        when {
            skipPathRequest(request, pathsToSkip) -> {
                SecurityContextHolder.getContext().authentication = AnonAuthentication()
                chain.doFilter(wrapRequest, response)
            }
            else -> {
                val authToken = tokenHelper.getToken(request)
                val usernameTry = tokenHelper.getUsernameFromToken(authToken)
                if (usernameTry.isSuccess) {
                    val (username, clientId) = usernameTry.getOrNull()!!.split("@@")
                    val userDetails = userDetailsService.loadUserByUsernameAndClientId(username, clientId)
                    val authentication = TokenBasedAuthentication(userDetails)
                    authentication.token = authToken
                    SecurityContextHolder.getContext().authentication = authentication
                    chain.doFilter(wrapRequest, response)
                } else {
                    val clientId = request.getParameter("clientId")
                    // val header = request.getHeader("X-Forwarded-Host")
                    if (clientId == null) {
                        loginExpired(request, response, usernameTry.exceptionOrNull()!!.message!!)
                    } else {

                        //val option = userService.loadAuthentication(clientId)
                        when (val option = userService.loadAuthenticationByClientId(clientId)) {
                            is Some -> {
                                SecurityContextHolder.getContext().authentication = option.t
                                chain.doFilter(wrapRequest, response)
                            }
                            None -> {
                                loginExpired(request, response, usernameTry.exceptionOrNull()!!.message!!)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loginExpired(request: HttpServletRequest, response: HttpServletResponse, message: String) {
        logger.warn(request.method + request.requestURI)
        val msg = objectMapper.writeValueAsString(ErrorDTO(message="login expired"))
        response.status = HttpStatus.FORBIDDEN.value()
        response.writer.write(msg)
    }

    private fun skipPathRequest(request: HttpServletRequest, pathsToSkip: List<String>): Boolean {
        val m = pathsToSkip.map { AntPathRequestMatcher(it) }
        return OrRequestMatcher(m).matches(request)
    }

    companion object {


        private fun getClientIp(request: HttpServletRequest): String {
            return request.getHeader("X-Forwarded-For").toOption().getOrElse { request.remoteAddr }
        }
    }
}