package com.github.b1412.permission.controller

import com.github.b1412.permission.controller.base.BasePermissionController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.github.b1412.permission.service.PermissionService

@RestController
@RequestMapping("/v1/permission")
class PermissionController : BasePermissionController()