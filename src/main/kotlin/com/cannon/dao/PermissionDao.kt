package com.cannon.dao


import com.cannon.entity.Permission
import com.cannon.dao.base.BaseDao
import org.springframework.stereotype.Repository

@Repository
interface PermissionDao : BaseDao<Permission, Long> {

    fun findByHttpMethod(httpMethod: String): Permission

}