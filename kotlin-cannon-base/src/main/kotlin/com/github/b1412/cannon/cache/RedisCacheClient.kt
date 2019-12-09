package com.github.b1412.cannon.cache

import arrow.core.Option
import arrow.core.Try
import arrow.core.getOrElse
import arrow.core.toOption
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit


@Component
class RedisCacheClient : CacheClient {

    @Autowired
    private val template: RedisTemplate<String, Any>? = null


    override fun <T> get(key: String): T? {
        val cache = Try { template!!.opsForValue().get(key) as T? }
        return when (cache) {
            is Try.Success -> cache.value
            is Try.Failure -> null
        }
    }

    override fun <T> get(key: String, supplier: () -> T): T? {
        val cache = Try { template!!.opsForValue().get(key) as T? }
        return when (cache) {
            is Try.Success -> cache.value.toOption()
            is Try.Failure -> {
                logger.error("Query key [{}] from cache failure", key, cache.exception.message)
                Option.empty()
            }
        }.getOrElse {
            val value = supplier()
            logger.debug("Query key [{}] from cache success, value [{}] ", key, value)
            template!!.opsForValue().set(key, value as Any)
            value
        }
    }

    override fun <T> get(key: String, supplier: () -> T, expire: Long): T? {
        val value = get(key, supplier)
        template!!.expire(key, expire, TimeUnit.SECONDS)
        return value
    }


    override fun set(key: String, value: Any) {
        template!!.opsForValue().set(key, value)
    }


    override fun set(key: String, expire: Long, value: Any) {
        set(key, value)
        template!!.expire(key, expire, TimeUnit.SECONDS)
    }

    override fun deleteByKey(key: String) {
        template!!.delete(key)
    }

    override fun deleteByPattern(pattern: String) {
        val keys = template!!.keys(pattern)
        for (key in keys) {
            logger.debug("Delete key [{}] in cache", key)
            template.delete(key)
        }
    }

    override fun keys(pattern: String): Set<String> {
        /*val connection = template!!.connectionFactory.connection
        val options = ScanOptions.scanOptions().match(pattern).count(Long.MAX_VALUE).build()
        val cursor = connection.scan(options)
        val keys = HashSet<String>()
        cursor.forEachRemaining { keys.add(String(it)) }*/
        return template!!.keys(pattern)
        //return keys
    }

    companion object {
        private val logger = LoggerFactory.getLogger(RedisCacheClient::class.java)
    }


}
