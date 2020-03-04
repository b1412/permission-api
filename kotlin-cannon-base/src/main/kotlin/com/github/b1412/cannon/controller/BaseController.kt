package com.github.b1412.cannon.controller

import arrow.core.Try
import arrow.core.toOption
import com.github.b1412.cannon.entity.BaseEntity
import com.github.b1412.cannon.entity.Branch
import com.github.b1412.cannon.entity.Role
import com.github.b1412.cannon.entity.User
import com.github.b1412.cannon.exceptions.ResultNotFoundException
import com.github.b1412.cannon.extenstions.copyFrom
import com.github.b1412.cannon.extenstions.responseEntityOk
import com.github.b1412.cannon.service.base.BaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import java.io.Serializable
import javax.servlet.http.HttpServletRequest

abstract class BaseController<T, ID : Serializable> {


    @Autowired
    lateinit var baseService: BaseService<T, ID>


    protected val loginUser: User
        get() = SecurityContextHolder.getContext().authentication.principal as User


    open fun page(request: HttpServletRequest, @RequestParam filter: Map<String, String>,pageable:Pageable): ResponseEntity<*> {
        val page = baseService.searchBySecurity(request.method, request.requestURI, filter,pageable)
        return page.responseEntityOk()
    }


    open fun findOne(@PathVariable id: ID, request: HttpServletRequest): ResponseEntity<*> {
        return baseService.findByIdOrNull(id).toOption()
                .fold(
                        { throw ResultNotFoundException() },
                        { it }
                ).responseEntityOk()
    }


    open fun saveOne(@Validated @RequestBody input: T, request: HttpServletRequest): ResponseEntity<*> {
        baseService.syncSeleceOneFromDb(input as BaseEntity)
        return ResponseEntity.ok(baseService.save(input))
    }


    open fun updateOne(@PathVariable id: ID, @Validated @RequestBody input: T, request: HttpServletRequest): ResponseEntity<*> {
        baseService.syncSeleceOneFromDb(input as BaseEntity)
        val persisted = baseService.findByIdOrNull(id)
        val merged = (persisted as Any).copyFrom(input) as T
        baseService.save(merged)
        return ResponseEntity.noContent().build<T>()
    }


    open fun deleteOne(@PathVariable id: ID, request: HttpServletRequest): ResponseEntity<*> {
        return Try { baseService.deleteById(id) }
                .fold(
                        { throw ResultNotFoundException() },
                        { ResponseEntity.noContent().build<Branch>() }
                )
    }


}