package com.github.b1412.security

import org.springframework.security.access.AccessDecisionManager
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class MyAccessDecisionManager : AccessDecisionManager {

    @Throws(AccessDeniedException::class, InsufficientAuthenticationException::class)
    override fun decide(authentication: Authentication, `object`: Any, configAttributes: Collection<ConfigAttribute>) {
        if (configAttributes.isEmpty() || authentication is AnonAuthentication) {
            return
        }
        var c: ConfigAttribute
        var needRole: String
        for (configAttribute in configAttributes) {
            c = configAttribute
            needRole = c.attribute
            for (ga in authentication.authorities) {
                if (needRole.trim { it <= ' ' }.equals(ga.authority, ignoreCase = true)) {
                    return
                }
            }
        }
        throw AccessDeniedException("no permission")
    }

    override fun supports(attribute: ConfigAttribute): Boolean {
        return true
    }

    override fun supports(clazz: Class<*>): Boolean {
        return true
    }
}