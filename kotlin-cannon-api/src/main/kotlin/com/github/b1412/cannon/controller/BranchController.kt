package com.github.b1412.cannon.controller

import arrow.core.Try
import arrow.core.toOption
import com.github.b1412.cannon.entity.Blog
import com.github.b1412.cannon.entity.Branch
import com.github.b1412.cannon.exceptions.ResultNotFoundException
import com.github.b1412.cannon.service.BranchService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/v1/branch")
class BranchController(
        val branchService: BranchService
) {

    @GetMapping
    fun list(@RequestParam filter: Map<String, String>, req: HttpServletRequest): List<Branch> =
            branchService.searchBySecurity(req.method, req.requestURI, filter)

    @GetMapping("{id}")
    fun findOne(@PathVariable id: Long, req: HttpServletRequest): Branch {
        return branchService.findByIdOrNull(id)
                .toOption()
                .fold(
                        { throw ResultNotFoundException() },
                        { it }
                )
    }

    @PostMapping
    fun saveOne(@RequestBody input: Branch) = branchService.save(input)

    @PutMapping("{id}")
    fun updateOne(@PathVariable id: Long, @RequestBody input: Blog): Branch {
        val persisted = branchService.findByIdOrNull(id)
        persisted.toOption()
                .fold(
                        { throw ResultNotFoundException() },
                        {
                            branchService.save(it)
                            return it
                        }
                )
    }

    @DeleteMapping("{id}")
    fun deleteOne(@PathVariable id: Long): ResponseEntity<Branch> {
        return Try { branchService.deleteById(id) }
                .fold(
                        { throw ResultNotFoundException() },
                        { ResponseEntity.noContent().build<Branch>() }
                )
    }
}