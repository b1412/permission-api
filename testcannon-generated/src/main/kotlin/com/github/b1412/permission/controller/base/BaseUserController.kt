package com.github.b1412.permission.controller.base

import com.github.leon.aci.util.QueryBuilder
import com.github.b1412.permission.controller.base.BaseUserController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.github.b1412.permission.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import com.github.leon.aci.web.base.BaseController
import com.github.leon.files.PoiExporter
import com.github.leon.bean.JpaBeanUtil
import org.springframework.transaction.annotation.Transactional


abstract class BaseUserController(

) : BaseController<User, Long>() {

    @PostMapping("graph")
    override fun graph(@RequestBody body: String, pageable: Pageable, request: HttpServletRequest): ResponseEntity<Page<User>> {
        val map = QueryBuilder.queryList(QueryBuilder.graphqlPlayload(body))
        val page = baseService.findBySecurity(request.method, request.requestURI, map, pageable)
        return ResponseEntity.ok(page)
    }

    @GetMapping("easyui")
    override fun easyui(pageable: Pageable, request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        val page = baseService.findByRequestParameters(request.parameterMap, pageable)
        val total = page.totalElements
        val rows = page.content
        val map = mapOf("total" to total, "rows" to rows)
        return ResponseEntity.ok(map)
    }

    @GetMapping
    override fun page(pageable: Pageable, request: HttpServletRequest): ResponseEntity<Page<User>> {
        return super.page(pageable, request)
    }

    @GetMapping("{id}")
    override fun findOne(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<User> {
        return super.findOne(id, request)
    }

    @Transactional
    @PostMapping
    override fun saveOne(@Validated @RequestBody input: User, request: HttpServletRequest): ResponseEntity<*> {
        return super.saveOne(input, request)
    }

    @Transactional
    @PutMapping("{id}")
    override fun updateOne(@PathVariable id: Long, @Validated @RequestBody input: User, request: HttpServletRequest): ResponseEntity<*> {
        return super.updateOne(id, input, request)

    }

    @DeleteMapping("{id}")
    override fun deleteOne(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<*> {
        return super.deleteOne(id, request)
    }

}