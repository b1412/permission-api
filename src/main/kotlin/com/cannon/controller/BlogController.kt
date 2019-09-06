package com.cannon.controller

import arrow.core.Try
import arrow.core.toOption
import com.cannon.bean.Blog
import com.cannon.dao.BlogDao
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
    fun list(): List<Blog> {
        return blogDao.findAll()
    }


    @GetMapping("{id}")
    fun findOne(@PathVariable id: Long, req: HttpServletRequest): Blog {
        return blogDao.findByIdOrNull(id)
                .toOption()
                .fold(
                        { throw ResponseStatusException(HttpStatus.NOT_FOUND) },
                        { it }
                )
    }

    @PostMapping
    fun saveOne(@RequestBody input: Blog): Blog {
        return blogDao.save(input)
    }

    @PutMapping("{id}")
    fun updateOne(@PathVariable id: Long, @RequestBody input: Blog): Blog {
        val persisted = blogDao.findByIdOrNull(id)
        persisted.toOption()
                .fold(
                        { throw ResponseStatusException(HttpStatus.NOT_FOUND) },
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
                        { throw ResponseStatusException(HttpStatus.NOT_FOUND) },
                        { ResponseEntity.noContent().build<Blog>() }
                )
    }
}