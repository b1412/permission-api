package com.cannon.controller

import arrow.core.Try
import arrow.core.toOption
import com.cannon.dao.BlogDao
import com.cannon.entity.Blog
import com.cannon.exceptions.ResultNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/blog")
class BlogController(
        val blogDao: BlogDao
) {

    @GetMapping
    fun list(): List<Blog> = blogDao.findAll()

    @GetMapping("{id}")
    fun findOne(@PathVariable id: Long, req: HttpServletRequest): Blog {
        return blogDao.findByIdOrNull(id)
                .toOption()
                .fold(
                        { throw ResultNotFoundException() },
                        { it }
                )
    }

    @PostMapping
    fun saveOne(@RequestBody input: Blog) = blogDao.save(input)

    @PutMapping("{id}")
    fun updateOne(@PathVariable id: Long, @RequestBody input: Blog): Blog {
        val persisted = blogDao.findByIdOrNull(id)
        persisted.toOption()
                .fold(
                        { throw ResultNotFoundException() },
                        {
                            it.title = input.title
                            blogDao.save(it)
                            return it
                        }
                )
    }

    @DeleteMapping("{id}")
    fun deleteOne(@PathVariable id: Long): ResponseEntity<Blog> {
        return Try { blogDao.deleteById(id) }
                .fold(
                        { throw ResultNotFoundException() },
                        { ResponseEntity.noContent().build<Blog>() }
                )
    }
}