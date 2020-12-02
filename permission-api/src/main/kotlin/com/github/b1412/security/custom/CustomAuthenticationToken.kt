package com.github.b1412.security.custom

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class CustomAuthenticationToken : UsernamePasswordAuthenticationToken {

    var domain: String? = null
        private set

    constructor(principal: Any, credentials: Any, domain: String) : super(principal, credentials) {
        this.domain = domain
        super.setAuthenticated(false)
    }

    constructor(
        principal: Any, credentials: Any, domain: String,
        authorities: Collection<GrantedAuthority>
    ) : super(principal, credentials, authorities) {
        this.domain = domain
        super.setAuthenticated(true) // must use super, as we override
    }
}