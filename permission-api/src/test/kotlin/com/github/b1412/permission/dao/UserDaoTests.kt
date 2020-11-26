package com.github.b1412.permission.dao

import com.github.b1412.permission.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.stat.Statistics
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import javax.persistence.EntityManager


class UserDaoTests : AbstractJpaTest() {

    @Autowired
    lateinit var userDao: UserDao

    @Autowired
    lateinit var roleDao: RoleDao

    @Autowired
    lateinit var entityManager: EntityManager

    lateinit var statistics: Statistics


    @BeforeEach
    fun setup() {
        //given
        val user = User(login = "a nice login", address = "address", email = "email", notes = "notes", clientId = "1")
        val user2 = User(login = "a nice login 2", address = "address 2a", email = "email 2b", notes = "notes 2c")
        userDao.save(user)
        userDao.save(user2)
    }

    @Test
    fun `search By filter`() {
        // when
        val users = userDao.searchByFilter(mapOf("email_like" to "emai"), Pageable.unpaged())
        // then
        assertThat(users.totalElements).isEqualTo(2)
    }

    @Test
    fun `search By filter 2`() {
        // when
        val users = userDao.searchByFilter(mapOf("email_eq" to "email"), Pageable.unpaged())
        // then
        assertThat(users.totalElements).isEqualTo(1)
    }

    @Test
    fun `search By filter 3`() {
        // when
        val users = userDao.searchByFilter(mapOf("id_gt" to "1"), Pageable.unpaged())
        // then
        assertThat(users.totalElements).isEqualTo(1)
    }

    @Test
    fun `search By filter 4`() {
        // when
        val users = userDao.searchByFilter(mapOf("id_lt" to "2"), Pageable.unpaged())
        // then
        assertThat(users.totalElements).isEqualTo(1)
    }

    @Test
    fun `search By filter 5`() {
        // when
        val users = userDao.searchByFilter(mapOf("clientId_null" to "null"), Pageable.unpaged())
        // then
        assertThat(users.totalElements).isEqualTo(1)
        assertThat(users.content[0].id).isEqualTo(2)
    }

    @Test
    fun `search By filter 6`() {
        // when
        val users = userDao.searchByFilter(mapOf("clientId_nn" to "notnull"), Pageable.unpaged())
        // then
        assertThat(users.totalElements).isEqualTo(1)
        assertThat(users.content[0].id).isEqualTo(1)
    }
}
