package com.github.b1412.cannon.dao

import com.github.b1412.cannon.entity.User
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import javax.persistence.EntityManager


class BaseEntityTest : AbstractJpaTest() {

    @Autowired
    lateinit var blogDao: UserDao
    @Autowired
    lateinit var entityManager: EntityManager


    @BeforeEach
    fun setup() {
        println(">> Before each")
        //given
        val blog = User(login = "Spring Framework 4.0 goes GA",address = "",email = "",notes = "")
        blogDao.save(blog)
    }


    @Test
    fun `test createdAt, updatedAt and version field values from base entity be created when create a new blog`() {
        blogDao.findAll().forEach {
            println("blog Id" + it.id)
        }
        // when
        val found = blogDao.findByIdOrNull(1)!!
        // then
        Assertions.assertThat(found.createdAt).isNotNull()
        Assertions.assertThat(found.updatedAt).isNotNull()
        Assertions.assertThat(found.version).isEqualTo(0)
    }

    @Test
    fun `test updatedAt and version field values from base entity be updated when update an existing blog`() {
        // when
        val found = blogDao.findByIdOrNull(1)!!
        val beforeCreateAt = found.createdAt
        val beforeUpdatedAt = found.updatedAt
        val beforeVersion = found.version
        found.login = "updated title"
        blogDao.save(found)
        entityManager.flush()
        // then
        Assertions.assertThat(beforeCreateAt).isEqualTo(found.createdAt)
        Assertions.assertThat(beforeUpdatedAt).isBefore(found.updatedAt)
        Assertions.assertThat(found.version!!).isEqualTo(beforeVersion!!.inc())
    }


}