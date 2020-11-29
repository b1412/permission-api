package com.github.b1412.permission.service

import com.github.b1412.api.service.BaseService
import com.github.b1412.permission.dao.RoleDao
import com.github.b1412.permission.entity.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class RoleService(
        @Autowired
        val dao: RoleDao
) : BaseService<Role, Long>(dao = dao)



