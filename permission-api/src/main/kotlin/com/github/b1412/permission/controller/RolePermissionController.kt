package com.github.b1412.permission.controller

import com.github.b1412.permission.controller.base.BaseRolePermissionController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/role-permission")
class RolePermissionController : BaseRolePermissionController()