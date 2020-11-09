package com.github.b1412.permission.service.rule.access

import com.github.b1412.permission.entity.Permission


interface AccessRule {

    val ruleName: String

    fun exec(permission: Permission): Map<String, String>
}
