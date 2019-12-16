package com.github.b1412.cannon.service.rule.access

import com.github.b1412.cannon.entity.Permission


interface AccessRule {

    val ruleName: String

    fun exec(permission: Permission): Map<String, String>
}
