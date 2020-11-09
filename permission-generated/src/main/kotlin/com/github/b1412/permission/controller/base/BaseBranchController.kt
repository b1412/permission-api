package com.github.b1412.permission.controller.base

import com.github.b1412.api.controller.BaseController
import com.github.b1412.permission.entity.Branch
import com.github.b1412.json.GraphRender
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest


@Transactional
abstract class BaseBranchController : BaseController<Branch, Long>() {

    @GraphRender("branch")
    @GetMapping
    override fun page(request: HttpServletRequest, @RequestParam filter: Map
    <String, String>, pageable: Pageable): ResponseEntity<*> {
        return super.page(request, filter, pageable)

    }

    @GraphRender("branch")
    @GetMapping("{id}")
    override fun findOne(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<*> {
        return super.findOne(id, request)
    }

    @PostMapping
    override fun saveOne(@Validated @RequestBody input: Branch, request: HttpServletRequest): ResponseEntity<*> {
        return super.saveOne(input, request)

    }

    @PutMapping("{id}")
    override fun updateOne(@PathVariable id: Long, @Validated @RequestBody input: Branch, request: HttpServletRequest): ResponseEntity<*> {
        super.updateOne(id, input, request)
        return ResponseEntity.noContent().build<Branch>()
    }

    @DeleteMapping("{id}")
    override fun deleteOne(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<*> {
        return super.deleteOne(id, request)

    }
}
