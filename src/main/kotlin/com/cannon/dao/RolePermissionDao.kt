package com.cannon.dao


import com.cannon.entity.RolePermission
import com.cannon.dao.base.BaseDao
import org.springframework.stereotype.Repository

@Repository
interface RolePermissionDao : BaseDao<RolePermission, Long>