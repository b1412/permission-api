package com.github.b1412.cannon.dao.base

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable

@NoRepositoryBean
interface BaseDao<T, ID : Serializable> : JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    fun searchByKeyword(keyword: String, fields: String): List<T>

    fun searchByFilter(filter: Map<String, String>): List<T>
}

