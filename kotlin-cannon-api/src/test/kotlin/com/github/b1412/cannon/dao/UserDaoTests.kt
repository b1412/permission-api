package com.github.b1412.cannon.dao

import com.github.b1412.cannon.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable


class UserDaoTests : AbstractJpaTest() {

    @Autowired
    lateinit var userDao: UserDao

    @BeforeEach
    fun setup() {
        //given
        val user = User(login = "a nice login", address = "address", email = "email", notes = "notes")
        val user2 = User(login = "a nice login 2", address = "address 2a", email = "email 2b", notes = "notes 2c")
        userDao.save(user)
        userDao.save(user2)
    }

    @Test
    fun `return a user when the login exist`() {
        //when
        val user = userDao.findByLogin("a nice login")
        //then
        assertThat(user).isNotNull
        assertThat(user!!.id).isEqualTo(1)
    }

    @Test
    fun `return null user when the login doesn't exist`() {
        //when
        val user = userDao.findByLogin("a non-exist login")
        //then
        assertThat(user).isNull()
    }

    @Test
    fun `return users when searching keyword matching address field`() {
        // when
        val users = userDao.searchByKeyword("addre", "address,email,notes")

        // then
        assertThat(users.size).isEqualTo(2)
    }

    @Test
    fun `return users when searching keyword matching email field`() {
        // when
        val users = userDao.searchByKeyword("emai", "address,email,notes")

        // then
        assertThat(users.size).isEqualTo(2)
    }

    @Test
    fun `return users when searching keyword matching notes field`() {
        // when
        val users = userDao.searchByKeyword("note", "address,email,notes")
        // then
        assertThat(users.size).isEqualTo(2)
    }


    @Test
    fun `return null user when searching keyword not matching any field`() {
        // when
        val users = userDao.searchByKeyword("null", "address,email,notes")
        // then
        assertThat(users.size).isEqualTo(0)
    }

    @Test
    fun `search By filter`() {
        // when
        val users = userDao.searchByFilter(mapOf("f_email" to "emai", "f_email_op" to "like"), Pageable.unpaged())
        // then
        assertThat(users.size).isEqualTo(2)
    }

    @Test
    fun `search By filter 2`() {
        // when
        val users = userDao.searchByFilter(mapOf("f_email" to "email"),Pageable.unpaged())
        // then
        assertThat(users.size).isEqualTo(1)
    }

}