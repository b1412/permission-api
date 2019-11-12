package com.github.b1412.cannon.service

import arrow.core.*
import com.github.b1412.cache.CacheClient
import com.github.b1412.cannon.dao.UserDao
import com.github.b1412.cannon.entity.Role
import com.github.b1412.cannon.entity.RolePermission
import com.github.b1412.cannon.entity.User
import com.github.b1412.security.ApplicationProperties
import com.github.b1412.security.TokenBasedAuthentication
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager


@Service
@EnableConfigurationProperties(value = [(ApplicationProperties::class)])
class UserService(
        @Autowired
        val userDao: UserDao,
        @Autowired
        val applicationProperties: ApplicationProperties,
        @Value("\${spring.application.name}")
        val application: String,

        @Autowired
        val cacheClient: CacheClient,
        @Autowired
        val entityManager: EntityManager
)  {

    val log = LoggerFactory.getLogger(UserService::class.java)!!

    @Transactional
    fun getUserWithPermissions(username: String, clientId: String): User {
        val id = userDao.findByUsernameAndClientId(username, clientId).toOption().map { it.id }.getOrElse { 0L }
        entityManager.clear()
        val graph = this.entityManager.createEntityGraph(User::class.java)

        val roleSubGraph = graph.addSubgraph<Role>("role")
        val rolePermissionSubGraph = roleSubGraph.addSubgraph<List<RolePermission>>("rolePermissions")
        rolePermissionSubGraph.addAttributeNodes("permission")
        rolePermissionSubGraph.addAttributeNodes("rules")

        val hints = HashMap<String, Any>()
        hints["javax.persistence.fetchgraph"] = graph
        val userOpt = this.entityManager.find(User::class.java, id, hints).toOption();
        val user = when (userOpt) {
            is Some -> userOpt.t
            None -> throw AccessDeniedException("invalid user information or user is not verified: $username")
        }
        val permissions = user.role!!.rolePermissions.map { it.permission }
        val grantedAuthorities = permissions.map { SimpleGrantedAuthority(it!!.authKey) as GrantedAuthority }.toMutableList()
        user.grantedAuthorities = grantedAuthorities
        return user
    }

    fun getEmails(user: User): List<String> {
        return user.toOption()
                .flatMap { it.email.toOption() }
                .map { it.split(",") }
                .getOrElse { (emptyList()) }
    }


    @Transactional
    fun loadAuthenticationByClientId(clientId: String): Option<Authentication> {
        return userDao.findByIdOrNull(clientId.toLong()).toOption()
                .map {
                    val user = cacheClient.get("$application-${it.username}-$clientId".toLowerCase()) { getUserWithPermissions(it.username, clientId) }!!
                    TokenBasedAuthentication(user as UserDetails)
                }

    }
}
