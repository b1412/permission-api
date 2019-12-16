package com.github.b1412.cannon.exceptions

data class ApiResp(
        val code: Int? = null,
        var message: String? = null,
        var error: String? = null
)
