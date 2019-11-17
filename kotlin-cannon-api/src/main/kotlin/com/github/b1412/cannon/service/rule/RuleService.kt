package com.github.leon.aci.service.rule

import arrow.core.Option
import arrow.core.extensions.list.foldable.firstOption
import com.github.b1412.cannon.entity.Rule
import com.github.b1412.cannon.service.base.BaseService
import com.github.b1412.cannon.service.rule.access.AccessRule
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service

class RuleService : BaseService<Rule, Long>() {
    val log = LoggerFactory.getLogger(RuleService::class.java)
    @Autowired
    lateinit var accessRules: List<AccessRule>

    fun findAccessRules(ruleName: String): Option<AccessRule> {
        log.debug("rule name {}", ruleName)
        return accessRules.firstOption { it.ruleName == ruleName }
    }
}
