package com.github.b1412.cannon.service

import com.github.b1412.cannon.service.base.BaseService
import com.github.b1412.cannon.dao.RoleDao
import com.github.b1412.cannon.entity.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class RoleService(
    @Autowired
    val dao: RoleDao
) : BaseService<Role, Long>(dao = dao)



