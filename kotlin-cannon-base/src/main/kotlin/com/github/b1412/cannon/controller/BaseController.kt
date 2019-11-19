package com.github.b1412.cannon.controller

import arrow.core.Try
import arrow.core.toOption
import com.github.b1412.cannon.entity.Branch
import com.github.b1412.cannon.entity.User
import com.github.b1412.cannon.exceptions.ResultNotFoundException
import com.github.b1412.cannon.extenstions.copyFrom
import com.github.b1412.cannon.service.base.BaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import java.io.Serializable
import javax.persistence.EntityManager
import javax.servlet.http.HttpServletRequest

abstract class BaseController<T, ID : Serializable> {

    @Autowired
    lateinit var entityManager: EntityManager
    @Autowired
    lateinit var baseService: BaseService<T, ID>


    protected val loginUser: User
        get() = SecurityContextHolder.getContext().authentication.principal as User


    open fun page(request: HttpServletRequest, @RequestParam filter: Map<String, String>): List<T> {
        val page = baseService.searchBySecurity(request.method, request.requestURI, filter)
        return page
    }


    open fun findOne(@PathVariable id: ID, request: HttpServletRequest): T {
        return baseService.findByIdOrNull(id).toOption()
                .fold(
                        { throw ResultNotFoundException() },
                        { it }
                )
    }


    open fun saveOne(@Validated @RequestBody input: T, request: HttpServletRequest): ResponseEntity<*> {
        return ResponseEntity.ok(baseService.save(input))
    }


    open fun updateOne(@PathVariable id: ID, @Validated @RequestBody input: T, request: HttpServletRequest): ResponseEntity<*> {
        val persisted = baseService.findByIdOrNull(id)
        val merged = (persisted as Any).copyFrom(input)
        baseService.save(merged)
        return ResponseEntity.ok(merged)
    }


    open fun deleteOne(@PathVariable id: ID, request: HttpServletRequest): ResponseEntity<*> {
        return Try { baseService.deleteById(id) }
                .fold(
                        { throw ResultNotFoundException() },
                        { ResponseEntity.noContent().build<Branch>() }
                )
    }


}