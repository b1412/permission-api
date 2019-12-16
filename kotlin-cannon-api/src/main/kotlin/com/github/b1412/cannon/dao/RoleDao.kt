package com.github.b1412.cannon.dao

import com.github.b1412.cannon.dao.base.BaseDao
import com.github.b1412.cannon.entity.Role
import org.springframework.stereotype.Repository

@Repository
interface RoleDao : BaseDao<Role, Long>
