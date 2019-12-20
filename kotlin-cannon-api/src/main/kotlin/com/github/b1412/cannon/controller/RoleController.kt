package com.github.b1412.cannon.controller

import arrow.core.toOption
import com.github.b1412.cannon.controller.base.BaseRoleController
import com.github.b1412.cannon.entity.Role
import com.github.b1412.cannon.exceptions.ResultNotFoundException
import com.github.b1412.cannon.extenstions.responseEntityOk
import com.github.b1412.cannon.json.GraphRender
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.transaction.Transactional


@RestController
@RequestMapping("/v1/role")
class RoleController : BaseRoleController() {

    private val roleConvert: (Role) -> MutableMap<String, Any> = { role ->
        val m = mutableMapOf<String, Any>()
        m["id"] = role.id.toString()
        m["name"] = role.name
        val groupBy = role.rolePermissions.groupBy { it.permission!!.entity }
        val menuIds = groupBy.keys.mapIndexed { index, s -> Pair(s!!, index.inc()) }.toMap()
        m["powers"] = groupBy
                .map { entry ->
                    val tempList = when (menuIds[entry.key]) {
                        3 -> listOf(1, 2, 3, 4, 5)
                        4 -> listOf(6, 7, 8, 9, 18)
                        5 -> listOf(10, 11, 12, 13)
                        6 -> listOf(14, 15, 16, 17)
                        else -> listOf()
                    }
                    val menus = mutableMapOf(
                            "menuId" to menuIds[entry.key],
                            "powers" to entry.value.map { it.permission!!.id } + tempList
                    )

                    menus
                }
        m
    }

    @GraphRender("role")
    @GetMapping
    @Transactional
    override fun page(request: HttpServletRequest, @RequestParam filter: Map<String, String>,pageable: Pageable): ResponseEntity<*> {
        val page = baseService.searchBySecurity(request.method, request.requestURI, filter,pageable)
        return page.map(roleConvert).responseEntityOk()
    }

    @GraphRender("role")
    @GetMapping("{id}")
    @Transactional
    override fun findOne(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<*> {
        return baseService.findByIdOrNull(id).toOption().map(roleConvert)
                .fold(
                        { throw ResultNotFoundException() },
                        { it }
                ).responseEntityOk()
    }

}