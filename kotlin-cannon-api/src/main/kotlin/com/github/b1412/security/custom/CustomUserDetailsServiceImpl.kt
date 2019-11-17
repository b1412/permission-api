package com.github.b1412.security.custom


import com.github.b1412.cache.CacheClient
import com.github.b1412.cannon.service.UserService
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service("userDetailsService")
class CustomUserDetailsServiceImpl(
        @Value("\${spring.application.name}")
        val application: String,
        @Autowired
        val userService: UserService,
        @Autowired
        val cacheClient: CacheClient
) : CustomUserDetailsService {

    val log = LoggerFactory.getLogger(CustomUserDetailsServiceImpl::class.java)!!

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsernameAndClientId(username: String, clientId: String): UserDetails {
        if (StringUtils.isAnyBlank(username, clientId)) {
            throw UsernameNotFoundException("Username and clientId must be provided")
        }
        log.info("clientId {},username {}", clientId, username)
        return cacheClient.get("$application-$username-$clientId".toLowerCase()) { userService.getUserWithPermissions(username, clientId) }!!
    }
}