package com.github.b1412.cannon.controller

import arrow.core.toOption
import com.github.b1412.cannon.controller.base.BaseRoleController
import com.github.b1412.cannon.entity.Role
import com.github.b1412.cannon.exceptions.ResultNotFoundException
import com.github.b1412.cannon.extenstions.responseEntityOk
import com.github.b1412.cannon.jpa.JpaBeanUtil
import com.github.b1412.cannon.json.GraphRender
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.transaction.Transactional

@RestController
@RequestMapping("/v1/role")
@Transactional
class RoleController : BaseRoleController() {

    @GraphRender("role")
    @GetMapping
    @Transactional
    override fun page(request: HttpServletRequest, @RequestParam filter: Map<String, String>, pageable: Pageable): ResponseEntity<*> {
        val page = baseService.searchBySecurity(request.method, request.requestURI, filter, pageable)
        return page.responseEntityOk()
    }


    @GraphRender("role")
    @GetMapping("{id}")
    override fun findOne(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<*> {
        return baseService.findByIdOrNull(id).toOption()
            .fold(
                { throw ResultNotFoundException() },
                { it }
            ).responseEntityOk()
    }

    //TODO pass rolePermission.id from FE
    @PutMapping("{id}")
    override fun updateOne(@PathVariable id: Long, @Validated @RequestBody input: Role, request: HttpServletRequest): ResponseEntity<*> {
        val oldRole = baseService.findByIdOrNull(id)!!
        input.rolePermissions.forEach {
            it.role = oldRole
            baseService.syncSeleceOneFromDb(it)
        }
        JpaBeanUtil.copyNonNullProperties(input, oldRole)

        oldRole.rolePermissions.clear()
        oldRole.rolePermissions.addAll(input.rolePermissions)
        baseService.save(oldRole)
        return ResponseEntity.noContent().build<Role>()
    }

}
