package com.github.b1412.permission.config

import com.github.b1412.permission.entity.User
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.*

@Component
class SecurityAuditor : AuditorAware<User> {

    override fun getCurrentAuditor(): Optional<User> {
        return Optional.of(SecurityContextHolder.getContext().authentication)
                .map { it.principal }
                .map { it as User }

    }
}
