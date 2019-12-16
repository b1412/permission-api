package com.github.b1412.cannon.controller.base

import com.github.b1412.cannon.controller.BaseController
import com.github.b1412.cannon.entity.User
import com.github.b1412.cannon.json.GraphRender
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest


abstract class BaseUserController : BaseController<User, Long>() {

    @GraphRender("user")
    @GetMapping
    override fun page(request: HttpServletRequest, @RequestParam filter: Map<String, String>): List<User> {
        return super.page(request, filter)

    }

    @GetMapping("{id}")
    override fun findOne(@PathVariable id: Long, request: HttpServletRequest): User {
        return super.findOne(id, request)
    }

    @Transactional
    @GraphRender("user")
    @PostMapping
    override fun saveOne(@Validated @RequestBody input: User, request: HttpServletRequest): ResponseEntity<*> {
        return super.saveOne(input, request)

    }

    @GraphRender("user")
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