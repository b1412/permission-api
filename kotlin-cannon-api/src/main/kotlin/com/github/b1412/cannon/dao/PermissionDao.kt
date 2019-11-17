package com.github.b1412.cannon.dao



import com.github.b1412.cannon.dao.base.BaseDao
import com.github.b1412.cannon.entity.Permission
import org.springframework.stereotype.Repository

@Repository
interface PermissionDao : BaseDao<Permission, Long>