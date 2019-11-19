package com.github.b1412.cannon.service

import com.github.b1412.cannon.service.base.BaseService
import com.github.b1412.cannon.dao.RolePermissionDao
import com.github.b1412.cannon.entity.RolePermission
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class RolePermissionService(
    @Autowired
    val dao: RolePermissionDao
) : BaseService<RolePermission, Long>(dao = dao)



