package com.github.b1412.security

import arrow.core.extensions.list.foldable.firstOption
import arrow.core.getOrElse
import com.github.b1412.cache.CacheClient
import com.github.b1412.permission.dao.PermissionDao
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.access.SecurityConfig
import org.springframework.security.web.FilterInvocation
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class MyInvocationSecurityMetadataSourceService(
    val permissionDao: PermissionDao,
    val cacheClient: CacheClient
) : FilterInvocationSecurityMetadataSource {
    /**
     * decide the url current request in the permission list, if so return to decide
     */
    override fun getAttributes(`object`: Any): List<ConfigAttribute> {
        val request = (`object` as FilterInvocation).httpRequest
        val configAttributes = cacheClient.get("permission-all-list", supplier = { permissionDao.findAll() })!!
            .firstOption { (_, _, authUris, httpMethod) ->
                authUris!!.split(";").any { Pattern.matches(it, request.requestURI) }
                        && request.method == httpMethod
            }
            .map { listOf(SecurityConfig(it.authKey)) }
            .getOrElse { listOf() }
        return configAttributes
    }

    override fun getAllConfigAttributes(): Collection<ConfigAttribute>? {
        return listOf()
    }

    override fun supports(clazz: Class<*>): Boolean {
        return true
    }
}
