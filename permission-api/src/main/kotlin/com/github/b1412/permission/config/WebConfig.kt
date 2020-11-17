package com.github.b1412.permission.config


import com.github.b1412.json.JsonReturnHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
        @Autowired
        val jsonReturnHandler: JsonReturnHandler
) : WebMvcConfigurer {
    override fun addReturnValueHandlers(returnValueHandlers: MutableList<HandlerMethodReturnValueHandler>) {
        returnValueHandlers.add(jsonReturnHandler)
    }
}