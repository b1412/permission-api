package com.cannon.controller

import com.cannon.entity.Blog
import com.cannon.dao.BlogDao
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@ContextConfiguration(classes = [BlogController::class])
@WebMvcTest
class BlogControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var blogDao: BlogDao

    @Test
    fun `return a list of blogs with 200 when blog exist`() {
        // given
        val blogA = Blog(title = "Title of Blog A").apply { this.id = 1 }
        val blogB = Blog(title = "Title of Blog B").apply { this.id = 2 }
        val mockedBlogs = listOf(blogA, blogB)
        every { blogDao.findAll() } returns mockedBlogs
        // when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/blog"))
        // then
        resultActions
                .andExpect(status().isOk)
                .andExpect(jsonPath("$[0].id", `is`(1)))
                .andExpect(jsonPath("$[0].title", `is`("Title of Blog A")))
                .andExpect(jsonPath("$[1].id", `is`(2)))
                .andExpect(jsonPath("$[1].title", `is`("Title of Blog B")))
    }

    @Test
    fun `return a blog with 200 when id exists`() {
        //given
        val blog = Blog(title = "Title of Blog").apply { this.id = 1 }
        every { blogDao.findByIdOrNull(1) } returns blog
        //when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/blog/1"))
        //then
        resultActions
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id", `is`(1)))
                .andExpect(jsonPath("$.title", `is`("Title of Blog")))
    }

    @Test
    fun `get blog return empty body with 404 when id doesn't exist`() {
        //given
        every { blogDao.findByIdOrNull(1) } returns null
        //when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/blog/1"))
        //then
        resultActions.andExpect(status().isNotFound)
    }


    @Test
    fun `save a new blog`() {
        //given
        val blog = Blog(title = "Title of Blog").apply { this.id = 1 }
        every { blogDao.save(any<Blog>()) } returns blog
        val body = """
  {
    "title": "Title of Blog"
  }
        """
        //when
        val resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/blog")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        )
        //then
        resultActions
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id", `is`(1)))
                .andExpect(jsonPath("$.title", `is`("Title of Blog")))
    }


    @Test
    fun `update blog return empty body with 404 when id doesn't exist`() {
        //given
        every { blogDao.findByIdOrNull(1) } returns null
        //when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/blog/1"))
        //then
        resultActions.andExpect(status().isNotFound)
    }

    @Test
    fun `update blog return updated blog with 200 when id exists`() {
        // given
        val persistedBlog = Blog(title = "Title of Blog").apply { this.id = 1 }
        val updatedBlog = Blog(title = "New Title of Blog").apply { this.id = 1 }
        every { blogDao.findByIdOrNull(1) } returns persistedBlog
        every { blogDao.save(any<Blog>()) } returns updatedBlog

        val body = """
        {
            "title": "New Title of Blog"
        }
"""
        // when
        val resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/blog/1")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        )
        // then
        resultActions
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id", `is`(1)))
                .andExpect(jsonPath("$.title", `is`("New Title of Blog")))
    }

    @Test
    fun `return 204 when delete an existing blog`() {
        //given
        every { blogDao.deleteById(1) } just Runs
        //when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/blog/1"))
        //then
        resultActions.andExpect(status().isNoContent)

    }

    @Test
    fun `return 404 when delete an non-existing blog`() {
        //given
        every { blogDao.deleteById(1) } throws EmptyResultDataAccessException(1)
        //when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/blog/1"))
        //then
        resultActions.andExpect(status().isNotFound)
    }
}