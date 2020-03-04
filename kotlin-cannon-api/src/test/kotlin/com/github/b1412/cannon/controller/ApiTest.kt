package com.github.b1412.cannon.controller

import com.github.b1412.cannon.DatabaseCleanupService
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApiTest {
    @Autowired
    private val mockMvc: MockMvc? = null
    @Autowired
    private val truncateDatabaseService: DatabaseCleanupService? = null

    @AfterEach
    fun cleanupAfterEach() {
        truncateDatabaseService!!.truncate()
    }

    @Test
    @Throws(Exception::class)
    fun get_category_return_200() { //given

        //when
        val resultActions =  mockMvc!!.perform(MockMvcRequestBuilders.post("/")).andDo(MockMvcResultHandlers.print())
        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].desc", Matchers.`is`("Happy")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].score", Matchers.`is`(5.0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[4].id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[4].desc", Matchers.`is`("Stressed out - not a happy camper")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[4].score", Matchers.`is`(1.0)))
    }

}