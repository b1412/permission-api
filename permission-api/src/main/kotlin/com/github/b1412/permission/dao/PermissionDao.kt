package com.github.b1412.permission.dao

import com.github.b1412.api.dao.BaseDao
import com.github.b1412.permission.entity.Permission
import org.springframework.stereotype.Repository

@Repository
interface PermissionDao : BaseDao<Permission, Long>
