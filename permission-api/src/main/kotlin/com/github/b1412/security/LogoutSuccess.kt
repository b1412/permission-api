package com.github.b1412.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.b1412.cache.CacheClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component

class LogoutSuccess(

    @Value("\${spring.application.name}")
    val application: String,
    @Autowired
    val objectMapper: ObjectMapper,

    val cacheClient: CacheClient
) : LogoutSuccessHandler {


    override fun onLogoutSuccess(
        httpServletRequest: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ) {
        if (authentication != null) {
            cacheClient.deleteByKey("$application-${authentication.name}")
        }
        val result = HashMap<String, String>()
        result["result"] = "success"
        response.contentType = "application/json"
        response.writer.write(objectMapper.writeValueAsString(result))
        response.status = HttpServletResponse.SC_OK

    }

}