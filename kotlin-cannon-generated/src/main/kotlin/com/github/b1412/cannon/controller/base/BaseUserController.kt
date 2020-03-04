package com.github.b1412.cannon.controller.base

import com.github.b1412.cannon.controller.BaseController
import org.springframework.web.bind.annotation.RestController
import com.github.b1412.cannon.entity.User
import com.github.b1412.cannon.json.GraphRender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestParam

abstract class BaseUserController : BaseController<User, Long>() {

    @GraphRender("user")
    @GetMapping
    override fun page(request: HttpServletRequest, @RequestParam filter: Map<String, String>, pageable: Pageable): ResponseEntity<*> {
        return super.page(request, filter,pageable)

    }

    @GraphRender("user")
    @GetMapping("{id}")
    override fun findOne(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<*> {
        return super.findOne(id, request)
    }

    @GraphRender("user")
    @Transactional
    @PostMapping
    override fun saveOne(@Validated @RequestBody input: User, request: HttpServletRequest): ResponseEntity<*> {
        return super.saveOne(input, request)

    }

    @GraphRender("user")
    @Transactional
    @PutMapping("{id}")
    override fun updateOne(@PathVariable id: Long, @Validated @RequestBody input: User, request: HttpServletRequest): ResponseEntity<*> {
        super.updateOne(id, input, request)
        return ResponseEntity.noContent().build<User>()
    }

    @GraphRender("user")
    @DeleteMapping("{id}")
    override fun deleteOne(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<*> {
        return super.deleteOne(id,request)

    }
}