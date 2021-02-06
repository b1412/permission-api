package com.github.b1412.api.controller

import arrow.core.toOption
import com.github.b1412.api.entity.BaseEntity
import com.github.b1412.api.service.BaseService
import com.github.b1412.extenstions.copyFrom
import com.github.b1412.extenstions.responseEntityOk
import com.github.b1412.permission.entity.User
import com.google.common.base.CaseFormat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.util.UriComponentsBuilder
import java.io.Serializable
import javax.servlet.http.HttpServletRequest

abstract class BaseController<T, ID : Serializable> {
    @Autowired
    lateinit var baseService: BaseService<T, ID>

    open fun page(
        request: HttpServletRequest,
        @RequestParam filter: Map<String, String>,
        pageable: Pageable
    ): ResponseEntity<*> {
        val page = baseService.searchBySecurity(request.method, request.requestURI, filter, pageable)
        return page.responseEntityOk()
    }

    open fun myPage(
        request: HttpServletRequest,
        @RequestParam filter: Map<String, String>,
        pageable: Pageable
    ): ResponseEntity<*> {
        val user = (SecurityContextHolder.getContext().authentication.principal as User)
        val page = baseService.searchBySecurity(
            request.method, request.requestURI, filter + mapOf("creator.id" to user.id.toString()), pageable
        )
        return page.responseEntityOk()
    }

    open fun findOne(@PathVariable id: ID, request: HttpServletRequest): ResponseEntity<*> {
        return baseService.findByIdOrNull(id).toOption()
            .fold(
                { ResponseEntity.notFound().build() },
                { it.responseEntityOk() }
            )
    }

    open fun saveOne(
        @Validated @RequestBody input: T,
        request: HttpServletRequest,
        uriComponent: UriComponentsBuilder
    ): ResponseEntity<*> {
        baseService.syncFromDb(input as BaseEntity)
        baseService.save(input)
        val endpoint =
            CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_HYPHEN).convert(input!!::class.java.simpleName)
        val uriComponents = uriComponent.path("/v1/${endpoint}/{id}").buildAndExpand(input.id)
        val headers = HttpHeaders()
        headers.location = uriComponents.toUri()
        return ResponseEntity.created(uriComponents.toUri()).build<Void>()
    }

    open fun updateOne(
        @PathVariable id: ID,
        @Validated @RequestBody input: T,
        request: HttpServletRequest
    ): ResponseEntity<*> {
        baseService.syncFromDb(input as BaseEntity)
        val persisted = baseService.findByIdOrNull(id)
        val merged = (persisted as Any).copyFrom(input) as T
        baseService.save(merged)
        return ResponseEntity.ok().build<T>()
    }

    open fun deleteOne(@PathVariable id: ID, request: HttpServletRequest): ResponseEntity<*> {
        baseService.deleteById(id)
        return ResponseEntity.noContent().build<T>()
    }

    open fun deleteAll(request: HttpServletRequest): ResponseEntity<*> {
        baseService.deleteAll()
        return ResponseEntity.noContent().build<T>()
    }
}
