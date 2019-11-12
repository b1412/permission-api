package com.github.b1412.cannon.dao

import com.github.b1412.cannon.entity.Blog
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull


class BlogDaoTests : AbstractJpaTest() {

    @Autowired
    lateinit var blogDao: BlogDao

    @BeforeEach
    fun setup() {
        println(">> Setup")
        //given
        val blog = Blog(title = "Spring Framework 4.0 goes GA")
        val blog2 = Blog(title = "Spring Framework 5.0 goes GA")
        blogDao.save(blog)
        blogDao.save(blog2)
    }

    @Test
    fun `When findAll then return all Blogs`() {
        //when
        val found = blogDao.findAll()
        //then
        assertThat(found.size).isEqualTo(2)
    }

    @Test
    fun `When findById then return the blog with right ID`() {
        //when
        val found = blogDao.findByIdOrNull(2)!!
        //then
        assertThat(found.id).isEqualTo(2)
        assertThat(found.title).isEqualTo("Spring Framework 5.0 goes GA")
    }

    @Test
    fun `return blogs when searching keyword matching title field`() {
        // when
        val blogs = blogDao.searchByKeyword(keyword = "Spring", fields = "title")
        // then
        assertThat(blogs.size).isEqualTo(2)
    }


    @Test
    fun `return null blog when searching keyword not matching any field`() {
        // when
        val blogs = blogDao.searchByKeyword(keyword = "null", fields = "title")
        // then
        assertThat(blogs.size).isEqualTo(0)
    }


}