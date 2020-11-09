package com.github.b1412.security.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.b1412.error.ErrorDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthenticationFailureHandler(
        @Autowired
        val objectMapper: ObjectMapper
) : SimpleUrlAuthenticationFailureHandler() {

    override fun onAuthenticationFailure(request: HttpServletRequest,
                                         response: HttpServletResponse,
                                         exception: AuthenticationException) {
        val msg = objectMapper.writeValueAsString(ErrorDTO(message = exception.message!!))
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.writer.write(msg)
    }
}