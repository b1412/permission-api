package com.github.leon.generator.entity

data class CodePermission(
        val role: String,
        val rule: String,
        val httpMethod: Array<String>
)