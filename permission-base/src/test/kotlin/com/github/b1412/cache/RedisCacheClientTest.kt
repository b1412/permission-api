package com.github.b1412.cache

import com.github.b1412.cache.RedisCacheClient
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.FixedHostPortGenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@ExtendWith(SpringExtension::class)
@Testcontainers
@ContextConfiguration(classes = [RedisCacheClient::class])
class RedisCacheClientTest {


    @Autowired
    private lateinit var redisCacheClient: RedisCacheClient

    @Test
    fun test() {
        // redisCacheClient.set("key", "value")
    }

    companion object {
        @Container
        private val container = FixedHostPortGenericContainer<Nothing>("redis:6.0.6")
                .apply {
                    withFixedExposedPort(6379, 6379)
                    start()
                }
    }
}

