package com.github.b1412.cannon.controller

import arrow.core.Try
import arrow.core.toOption
import com.github.b1412.cannon.dao.UserDao
import com.github.b1412.cannon.entity.User
import com.github.b1412.cannon.exceptions.ResultNotFoundException
import com.github.b1412.cannon.service.UserService

import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
        val userDao: UserDao,
        val userService: UserService
) {

    @GetMapping
    fun list(@RequestParam filter: Map<String, String>) = userService.searchByFilter(filter)


    @GetMapping("{id}")
    fun findOne(@PathVariable id: Long): User {
        return userDao.findByIdOrNull(id)
                .toOption()
                .fold(
                        { throw ResultNotFoundException() },
                        { it }
                )
    }

    @PostMapping
    fun saveOne(@RequestBody input: User) = userDao.save(input)

    @PutMapping("{id}")
    fun updateOne(@PathVariable id: Long, @RequestBody input: User): User {
        val persisted = userDao.findByIdOrNull(id)
        persisted.toOption()
                .fold(
                        { throw ResultNotFoundException() },
                        {
                            it.login = input.login
                            it.address = input.address
                            it.email = input.email
                            it.notes = input.notes
                            userDao.save(it)
                            return it
                        }
                )
    }

    @DeleteMapping("{id}")
    fun deleteOne(@PathVariable id: Long) =
            Try { userDao.deleteById(id) }
                    .fold(
                            { throw ResultNotFoundException() },
                            { ResponseEntity.noContent().build<User>() }
                    )

}