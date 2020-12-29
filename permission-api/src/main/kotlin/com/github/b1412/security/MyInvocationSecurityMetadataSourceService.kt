package com.github.b1412.security

import arrow.core.None
import arrow.core.Some
import arrow.core.extensions.list.foldable.firstOption
import com.github.b1412.cache.CacheClient
import com.github.b1412.permission.dao.PermissionDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.access.SecurityConfig
import org.springframework.security.web.FilterInvocation
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource
import org.springframework.stereotype.Service
import java.util.regex.Pattern
import javax.persistence.EntityManager

@Service
class MyInvocationSecurityMetadataSourceService(
    @Autowired
    val permissionDao: PermissionDao,
    @Autowired
    val cacheClient: CacheClient,
    @Autowired
    val entityManager: EntityManager
) : FilterInvocationSecurityMetadataSource {

    override fun getAttributes(`object`: Any): List<ConfigAttribute>? {
        val request = (`object` as FilterInvocation).httpRequest
        val permissionOpt = cacheClient.get("permission-all-list") {
            permissionDao.findAll()
        }!!.filter { (_, _, _, authUris) ->
            authUris != null
        }.firstOption { (_, _, _, authUris) ->
            authUris!!.split(";").any { uriPatten -> Pattern.matches(uriPatten, request.requestURI) }
        }

        return when (permissionOpt) {
            is Some -> {
                listOf(SecurityConfig(permissionOpt.t.authKey))
            }
            None -> null
        }
    }

    override fun getAllConfigAttributes(): Collection<ConfigAttribute>? {
        return null
    }

    override fun supports(clazz: Class<*>): Boolean {
        return true
    }
}
