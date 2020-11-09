package com.github.b1412.security


data class UserTokenState(
        var access_token: String? = null,
        var expires_in: Long? = null,
)