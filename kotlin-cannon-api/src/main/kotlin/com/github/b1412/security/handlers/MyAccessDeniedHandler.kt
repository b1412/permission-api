package com.github.b1412.security.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.b1412.cannon.exceptions.ApiResp
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class MyAccessDeniedHandler : AccessDeniedHandler {
    @Autowired
    private val objectMapper: ObjectMapper? = null

    override fun handle(request: HttpServletRequest, response: HttpServletResponse, accessDeniedException: AccessDeniedException) {
        val apiResp = ApiResp()
        apiResp.error = "AccessDenied: " + request.requestURI
        val msg = objectMapper!!.writeValueAsString(apiResp)
        response.status = 403
        response.writer.write(msg)
    }
}
