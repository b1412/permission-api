package com.cannon.dao

import com.cannon.bean.User
import com.cannon.dao.base.BaseDao
import org.springframework.stereotype.Repository

@Repository
interface UserDao : BaseDao<User, Long> {
    fun findByLogin(login: String): User?
}