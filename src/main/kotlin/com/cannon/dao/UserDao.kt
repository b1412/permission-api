package com.cannon.dao

import com.cannon.dao.base.BaseDao
import com.cannon.entity.User
import org.springframework.stereotype.Repository

@Repository
interface UserDao : BaseDao<User, Long> {
    fun findByLogin(login: String): User?
}