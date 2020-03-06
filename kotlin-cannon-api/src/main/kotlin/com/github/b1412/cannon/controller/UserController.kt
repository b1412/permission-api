package com.github.b1412.cannon.controller

import com.github.b1412.cannon.controller.base.BaseUserController
import com.github.b1412.cannon.entity.User
import com.github.b1412.cannon.json.GraphRender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@Transactional
@RestController
@RequestMapping("/v1/user")
class UserController(
        @Autowired
        val passwordEncoder: PasswordEncoder

) : BaseUserController() {

    @PostMapping
    override fun saveOne(@Validated @RequestBody input: User, request: HttpServletRequest): ResponseEntity<*> {
        if (input.password != input.confirmPassword) {
            throw  IllegalArgumentException("password not equal")
        }
        input.setUsername(input.email!!)
        input.setPassword(passwordEncoder.encode(input.password))
        return super.saveOne(input, request)
    }

    @PutMapping("{id}")
    override fun updateOne(@PathVariable id: Long, @Validated @RequestBody input: User, request: HttpServletRequest): ResponseEntity<*> {
        if (input.password != input.confirmPassword) {
            throw  IllegalArgumentException("password not equal")
        }
        input.setUsername(input.email!!)
        input.setPassword(passwordEncoder.encode(input.password))
        return super.updateOne(id, input, request)
    }

    @GraphRender("user")
    @GetMapping("me")
    fun me(request: HttpServletRequest): ResponseEntity<*> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        return super.findOne(user.id!!, request)
    }
}