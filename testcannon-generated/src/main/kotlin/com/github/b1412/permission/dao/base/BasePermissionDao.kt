package com.github.b1412.permission.dao.base

import com.github.leon.aci.dao.base.BaseDao
import com.github.b1412.permission.entity.Permission
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface BasePermissionDao : BaseDao<Permission, Long>
