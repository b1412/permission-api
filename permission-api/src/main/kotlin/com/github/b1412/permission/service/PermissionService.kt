package com.github.b1412.permission.service

import com.github.b1412.api.service.BaseService
import com.github.b1412.permission.dao.PermissionDao
import com.github.b1412.permission.entity.Permission
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class PermissionService(
    @Autowired
    val dao: PermissionDao
) : BaseService<Permission, Long>(dao = dao)



