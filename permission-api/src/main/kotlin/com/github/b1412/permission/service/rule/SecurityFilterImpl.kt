package com.github.b1412.permission.service.rule

import arrow.core.Either
import arrow.core.extensions.list.foldable.firstOrNone
import arrow.core.getOrElse
import com.github.b1412.api.service.SecurityFilter
import com.github.b1412.permission.entity.User
import com.github.b1412.permission.service.rule.access.AccessRule
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.text.MessageFormat
import java.util.regex.Pattern

@Component
class SecurityFilterImpl : SecurityFilter {

    @Autowired
    lateinit var accessRules: List<AccessRule>

    fun findAccessRules(ruleName: String): Either<Unit, AccessRule> {
        return accessRules.firstOrNone { it.ruleName == ruleName }.toEither { }
    }

    /**
     *  if no rule found, return all.
     */
    override fun query(method: String, requestURI: String): Map<String, String> {
        val role = (SecurityContextHolder.getContext().authentication.principal as User).role!!
        val permissions = role
            .rolePermissions
            .map { it.permission }
            .filter {
                it!!.authUris!!.split(";").any { uriPatten ->
                    Pattern.matches(uriPatten, requestURI)
                } && it.httpMethod == method
            }
        logger.debug("method $method url $requestURI has ${permissions.size} permission(s)")
        if (permissions.isEmpty()) {
            throw AccessDeniedException(MessageFormat.format("No permission {0} {1}", method, requestURI))
        }

        val permission = permissions.first()!!
        val rules = role.rolePermissions
            .firstOrNone { it.permission!!.id == permission.id }
            .map { it.rules }
            .getOrElse { listOf() }
        return when {
            rules.isEmpty() -> {
                arrow.core.mapOf()
            }
            else -> {
                rules.map { rule ->
                    val accessRule = findAccessRules(rule.name!!)
                    logger.debug("access rule {0}", accessRule)
                    accessRule.map { it.exec(permission) }.getOrElse { arrow.core.mapOf() }
                }.reduce { acc, map -> acc + map }
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SecurityFilterImpl::class.java)
    }

}
