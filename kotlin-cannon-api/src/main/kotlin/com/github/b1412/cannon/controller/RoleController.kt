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
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.transaction.Transactional

@RestController
@RequestMapping("/v1/role")
@Transactional
class RoleController : BaseRoleController() {

    private val roleConvert: (Role) -> MutableMap<String, Any> = { role ->
        val m = mutableMapOf<String, Any>()
        m["id"] = role.id!!
        m["name"] = role.name
        val groupBy = role.rolePermissions.groupBy { it.permission!!.entity }
        val powers = groupBy
                .map { entry ->
                    val menus = mutableMapOf(
                            // "menuId" to menuIds[entry.key],
                            "powers" to entry.value.map { it.permission!!.authKey }
                    )

                    menus
                }
        m["powers"] = powers
        m
    }


    @GraphRender("role")
    @GetMapping
    @Transactional
    override fun page(request: HttpServletRequest, @RequestParam filter: Map<String, String>, pageable: Pageable): ResponseEntity<*> {
        val page = baseService.searchBySecurity(request.method, request.requestURI, filter, pageable)
        return page.map(roleConvert).responseEntityOk()
    }


    @GraphRender("role")
    @GetMapping("{id}")
    override fun findOne(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<*> {
        return baseService.findByIdOrNull(id).toOption().map(roleConvert)
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