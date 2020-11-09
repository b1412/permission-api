package com.github.b1412.permission.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Bean
    fun redisTemplate(factory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.setConnectionFactory(factory)
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = JdkSerializationRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = JdkSerializationRedisSerializer()
        return template
    }
}