package com.github.b1412.cannon.service.rule.access

import com.github.b1412.cannon.service.rule.SecurityFilter
import org.springframework.beans.factory.annotation.Autowired


abstract class AbstractAccessRule : AccessRule {
    @Autowired
    protected var securityFilter: SecurityFilter? = null
}
