package com.github.b1412.cannon.cache


interface CacheClient {

    fun <T> get(key: String): T?

    fun <T> get(key: String, supplier: () -> T): T?

    fun <T> get(key: String, supplier: () -> T, expire: Long): T?

    fun set(key: String, value: Any)

    fun set(key: String, expire: Long, value: Any)

    fun deleteByKey(key: String)

    fun deleteByPattern(pattern: String)

    fun keys(pattern: String): Set<String>


}
