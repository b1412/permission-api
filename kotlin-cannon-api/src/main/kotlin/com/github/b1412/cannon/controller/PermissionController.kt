package com.github.b1412.cannon.controller

import com.github.b1412.cannon.controller.base.BasePermissionController
import com.github.b1412.cannon.service.SecurityFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/v1/permission")
class PermissionController(
        @Autowired
        val securityFilter: SecurityFilter
) : BasePermissionController() {

    @GetMapping("/filter")
    fun filter(@RequestParam method: String, @RequestParam uri: String) = securityFilter.query(method, uri)

}
