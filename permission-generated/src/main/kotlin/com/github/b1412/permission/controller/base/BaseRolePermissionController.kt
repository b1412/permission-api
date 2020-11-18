package com.github.b1412.permission.controller.base

import com.github.b1412.api.controller.BaseController
import com.github.b1412.json.GraphRender
import com.github.b1412.permission.entity.RolePermission
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import javax.servlet.http.HttpServletRequest

@Transactional
abstract class BaseRolePermissionController : BaseController<RolePermission, Long>() {

    @GraphRender("rolePermission")
    @GetMapping
    override fun page(request: HttpServletRequest, @RequestParam filter: Map<String, String>, pageable: Pageable): ResponseEntity<*> {
        return super.page(request, filter, pageable)
    }

    @GraphRender("rolePermission")
    @GetMapping("{id}")
    override fun findOne(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<*> {
        return super.findOne(id, request)
    }

    @PostMapping
    override fun saveOne(@Validated @RequestBody input: RolePermission, request: HttpServletRequest, uriComponent: UriComponentsBuilder): ResponseEntity<*> {
        return super.saveOne(input, request, uriComponent)
    }

    @PutMapping("{id}")
    override fun updateOne(@PathVariable id: Long, @Validated @RequestBody input: RolePermission, request: HttpServletRequest): ResponseEntity<*> {
        return super.updateOne(id, input, request)
    }

    @DeleteMapping("{id}")
    override fun deleteOne(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<*> {
        return super.deleteOne(id, request)
    }
}