package com.github.b1412.generator.entity

data class CodePermission(
        val role: String,
        val rule: String,
        val httpMethod: Array<String>
)