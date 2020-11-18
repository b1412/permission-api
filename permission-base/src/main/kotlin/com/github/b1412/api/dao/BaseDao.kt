package com.github.b1412.api.dao

import arrow.core.Either
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable

@NoRepositoryBean
interface BaseDao<T, ID : Serializable> : JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    fun searchByFilter(filter: Map<String, String>, pageable: Pageable): Page<T>

    fun searchOneBy(filter: Map<String, String>): Either<Unit, T>
}

