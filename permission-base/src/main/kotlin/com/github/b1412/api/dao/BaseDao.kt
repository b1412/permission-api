package com.github.b1412.api.dao


import arrow.core.Either
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable
import javax.persistence.EntityGraph


@NoRepositoryBean
interface BaseDao<T, ID : Serializable> : JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    fun findAll(spec: Specification<T>, pageable: Pageable, entityGraphType: EntityGraph<T>): Page<T>

    fun searchByFilter(filter: Map<String, String>, pageable: Pageable): Page<T>

    fun searchOneBy(filter: Map<String, String>): Either<Unit, T>

    fun searchOneByOrNull(filter: Map<String, String>): T?
}

