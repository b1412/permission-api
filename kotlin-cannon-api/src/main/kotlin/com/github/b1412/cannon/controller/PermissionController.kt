package com.github.b1412.cannon.controller

import com.github.b1412.cannon.controller.base.BasePermissionController
import com.github.b1412.cannon.extenstions.responseEntityOk
import com.github.b1412.cannon.json.GraphRender
import com.github.b1412.cannon.service.SecurityFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping("/v1/permission")
class PermissionController(
        @Autowired
        val securityFilter: SecurityFilter
) : BasePermissionController() {

    @GetMapping("/filter")
    fun filter(@RequestParam method: String, @RequestParam uri: String) = securityFilter.query(method, uri)


    @GraphRender("permission")
    @GetMapping("menu")
    fun menus(request: HttpServletRequest): ResponseEntity<*> {
        val permissions = baseService.findAll()
        val groupBy = permissions.groupBy { it.entity }.filter { it.key != "role-permission" }
        return groupBy
                .map { entry ->
                    val menu = entry.value.first()
                    mapOf(
                            "id" to menu.id,
                            "title" to menu.entity,
                            "icon" to "setting",
                            "url" to "/" + menu.entity!!.toLowerCase(),
                            "parent" to null,
                            "sorts" to 1,
                            "conditions" to 1,
                            "powers" to entry.value.map { rp ->
                                mapOf(
                                        "id" to rp.id,
                                        "menu" to menu.id,
                                        "title" to rp!!.display,
                                        "code" to rp!!.authKey,
                                        "sorts" to 1,
                                        "conditions" to 1
                                )
                            }
                    )

                }.responseEntityOk()
    }
}
