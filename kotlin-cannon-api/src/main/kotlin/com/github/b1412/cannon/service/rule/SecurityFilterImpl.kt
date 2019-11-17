package com.github.b1412.cannon.service.rule


import arrow.core.extensions.list.foldable.firstOption
import com.github.b1412.cannon.entity.User
import com.github.leon.aci.service.rule.RuleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class SecurityFilterImpl : SecurityFilter {


    @Autowired
    private val ruleService: RuleService? = null

    override fun currentUser(): User {
        return SecurityContextHolder.getContext().authentication.principal as User

    }

    override fun query(method: String, requestURI: String): Map<String, String> {

        val permission = currentUser().role!!
                .rolePermissions
                .map { it.permission }
                .firstOption {
                    it!!.authUris.split(";").any { uriPatten -> Pattern.matches(uriPatten, requestURI) }
                }
//        return when (permission.isDefined()) {
//            true -> {
//                currentUser().role!!.rolePermissions.firstOption { it.permission!!.id == permission.get()!!.id }.map { it.rules }
//                        .map {
//                            it.map { ruleService!!.findAccessRules(it.name) }.map { it.get().exec(permission.get()!!) }
//                        }.get()
//            }
//            false -> {
//                throw AccessDeniedException(MessageFormat.format("No permission {0} {1}", method, requestURI))
//            }
//        }
        throw IllegalArgumentException()
    }
}
