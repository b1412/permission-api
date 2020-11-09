package com.github.b1412.security.custom

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class CustomAuthenticationFilter : UsernamePasswordAuthenticationFilter() {

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse?): Authentication {
        if (request.method != "POST") {
            throw AuthenticationServiceException("Authentication method only support POST, but received" + request.method)
        }
        val sb = StringBuffer()
        var line: String?
        val reader = request.reader
        while (reader.readLine().also { line = it } != null) {
            sb.append(line)
        }
        val mapper = ObjectMapper().registerKotlinModule()
        val loginRequest = mapper.readValue<LoginRequest>(sb.toString())
        val authRequest = CustomAuthenticationToken(loginRequest.username, loginRequest.password, loginRequest.clientId)
        setDetails(request, authRequest)
        return this.authenticationManager.authenticate(authRequest)
    }
}