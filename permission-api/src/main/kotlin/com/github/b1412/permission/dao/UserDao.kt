package com.github.b1412.permission.dao

import com.github.b1412.api.dao.BaseDao
import com.github.b1412.permission.entity.User
import org.springframework.stereotype.Repository

@Repository
interface UserDao : BaseDao<User, Long> {
    fun findByUsernameAndClientId(username: String, domain: String): User?
}
