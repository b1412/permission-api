package com.github.b1412.security

data class UserTokenState(
    var userId: String? = null,
    var accessToken: String? = null,
    var expiresIn: Long? = null,
)