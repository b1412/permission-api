package com.github.b1412.cannon.controller

import com.github.b1412.cannon.dao.PermissionDao
import com.github.b1412.cannon.dao.RoleDao
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/v1/menu")
class MenuController(
        val roleDao: RoleDao,
        val permissionDao: PermissionDao
) {




    @Transactional
    @GetMapping("permissions")
    fun powers(@RequestParam menuId: Int?): MutableList<Map<String, Any?>> {
        val p = permissionDao.findByIdOrNull(menuId!!.toLong())!!
        return permissionDao.searchByFilter(
                mapOf(
                        "f_entity_op" to "=",
                        "f_entity" to p.entity!!),
                Pageable.unpaged()
        ).map {
            mapOf(
                    "menu" to menuId,
                    "title" to it.display,
                    "code" to it.authKey,
                    "sorts" to 1,
                    "conditions" to 1
            )
        }.content
    }
}


