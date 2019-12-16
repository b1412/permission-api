package com.github.b1412.security.custom

import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomAuthenticationFilter : UsernamePasswordAuthenticationFilter() {

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse?): Authentication {

        if (request.method != "POST") {
            throw AuthenticationServiceException("Authentication method not supported: " + request.method)
        }

        val authRequest = getAuthRequest(request)
        setDetails(request, authRequest)
        return this.authenticationManager.authenticate(authRequest)
    }

    private fun getAuthRequest(request: HttpServletRequest): CustomAuthenticationToken {
        var username: String? = obtainUsername(request)
        var password: String? = obtainPassword(request)
        var clientId: String? = obtainClientId(request)

        if (username == null) {
            username = ""
        }
        if (password == null) {
            password = ""
        }
        if (clientId == null) {
            clientId = ""
        }

        return CustomAuthenticationToken(username, password, clientId)
    }

    private fun obtainClientId(request: HttpServletRequest): String {
        return request.getParameter(SPRING_SECURITY_FORM_CLIENT_ID_KEY)
    }

    companion object {

        const val SPRING_SECURITY_FORM_CLIENT_ID_KEY = "clientId"
    }
}