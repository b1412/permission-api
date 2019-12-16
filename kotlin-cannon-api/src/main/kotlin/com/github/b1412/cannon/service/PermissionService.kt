package com.github.b1412.cannon.service

import com.github.b1412.cannon.dao.PermissionDao
import com.github.b1412.cannon.entity.Permission
import com.github.b1412.cannon.service.base.BaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class PermissionService(
        @Autowired
        val dao: PermissionDao
) : BaseService<Permission, Long>(dao = dao)



