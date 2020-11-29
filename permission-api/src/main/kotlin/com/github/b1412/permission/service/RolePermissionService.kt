package com.github.b1412.permission.service

import com.github.b1412.api.service.BaseService
import com.github.b1412.permission.dao.RolePermissionDao
import com.github.b1412.permission.entity.RolePermission
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class RolePermissionService(
        @Autowired
        val dao: RolePermissionDao
) : BaseService<RolePermission, Long>(dao = dao)



