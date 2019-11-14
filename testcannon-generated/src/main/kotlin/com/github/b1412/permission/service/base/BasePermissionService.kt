package com.github.b1412.permission.service.base


import com.github.b1412.permission.entity.Permission
import com.github.leon.aci.service.base.BaseService
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
class BasePermissionService : BaseService<Permission, Long>()

