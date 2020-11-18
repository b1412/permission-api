package com.github.b1412.api.service

interface SecurityFilter {
    fun query(method: String, requestURI: String): Map<String, String>
}
