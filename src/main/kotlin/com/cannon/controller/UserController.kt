package com.cannon.controller

import arrow.core.Try
import arrow.core.toOption
import com.cannon.dao.UserDao
import com.cannon.entity.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/user")
class UserController(
        val userDao: UserDao
) {

    @GetMapping
    fun list(@RequestParam filter: Map<String, String>) = userDao.searchByFilter(filter)


    @GetMapping("{id}")
    fun findOne(@PathVariable id: Long): User {
        return userDao.findByIdOrNull(id)
                .toOption()
                .fold(
                        { throw ResponseStatusException(HttpStatus.NOT_FOUND) },
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
                        { throw ResponseStatusException(HttpStatus.NOT_FOUND) },
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
                            { throw ResponseStatusException(HttpStatus.NOT_FOUND) },
                            { ResponseEntity.noContent().build<User>() }
                    )

}