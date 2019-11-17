package com.github.b1412.cannon.dao


import com.github.b1412.cannon.dao.base.BaseDao
import com.github.b1412.cannon.entity.User
import org.springframework.stereotype.Repository

@Repository
interface UserDao : BaseDao<User, Long> {
    fun findByLogin(login: String): User?
    fun findByUsernameAndClientId(username: String, domain: String): User?
}