package com.github.b1412.security

import com.github.b1412.extenstions.println
import org.springframework.security.access.AccessDecisionManager
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class MyAccessDecisionManager : AccessDecisionManager {

    @Throws(AccessDeniedException::class, InsufficientAuthenticationException::class)
    override fun decide(currentUser: Authentication, obj: Any, configAttributes: Collection<ConfigAttribute>) {
        if (configAttributes.isEmpty()
            || currentUser is AnonAuthentication
            || (configAttributes as List).first().toString() == "authenticated"
        ) {
            return
        }
        val hasPermission = configAttributes.all { configAttribute ->
            currentUser.authorities.any {
                it.authority.equals(configAttribute.attribute, ignoreCase = true)
            }
        }
        if (hasPermission) {
            return
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