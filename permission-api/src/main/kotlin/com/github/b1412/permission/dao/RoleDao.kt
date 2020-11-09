package com.github.b1412.permission.dao

import com.github.b1412.api.dao.BaseDao
import com.github.b1412.permission.entity.Role
import org.springframework.stereotype.Repository

@Repository
interface RoleDao : BaseDao<Role, Long>
