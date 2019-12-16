package com.github.b1412.security.custom

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

interface CustomUserDetailsService {

    @Throws(UsernameNotFoundException::class)
    fun loadUserByUsernameAndClientId(username: String, clientId: String): UserDetails

}