package com.github.b1412.security


import arrow.core.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.b1412.cannon.exceptions.ApiResp
import com.github.b1412.cannon.service.UserService
import com.github.b1412.security.custom.CustomUserDetailsService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Instant
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
        var pathsToSkip = applicationProperties.jwt.anonymousUrls.toOption()
                .map { it.split(",") }
                .map { it.toList() }
                .getOrElse { emptyList() }

        val authToken = tokenHelper.getToken(request)
        when {
            skipPathRequest(request, pathsToSkip) -> {
                SecurityContextHolder.getContext().authentication = AnonAuthentication()
                chain.doFilter(wrapRequest, response)
            }
            else -> {
                val usernameTry = tokenHelper.getUsernameFromToken(authToken)
                when (usernameTry) {
                    is Success -> {
                        val (username, clientId) = usernameTry.value.split("@@")
                        val userDetails = userDetailsService.loadUserByUsernameAndClientId(username, clientId)
                        val authentication = TokenBasedAuthentication(userDetails)
                        authentication.token = authToken
                        SecurityContextHolder.getContext().authentication = authentication
                        chain.doFilter(wrapRequest, response)
                    }
                    is Failure -> {
                        val clientId = request.getParameter("clientId")
                        // val header = request.getHeader("X-Forwarded-Host")
                        if (clientId == null) {
                            loginExpired(request, response, usernameTry.exception.message!!)
                        } else {

                            //val option = userService.loadAuthentication(clientId)
                            val option = userService.loadAuthenticationByClientId(clientId)
                            when (option) {
                                is Some -> {
                                    SecurityContextHolder.getContext().authentication = option.t
                                    chain.doFilter(wrapRequest, response)
                                }
                                None -> {
                                    loginExpired(request, response, usernameTry.exception.message!!)
                                }
                            }
                        }

                    }
                }
            }
        }

    }


    private fun loginExpired(request: HttpServletRequest, response: HttpServletResponse, message: String) {
        logger.warn(request.method + request.requestURI)
        val apiResp = ApiResp()
        apiResp.message = message
        apiResp.error = "login expired"
        val msg = objectMapper.writeValueAsString(apiResp)
        response.status = 403
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