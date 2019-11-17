package com.github.b1412.cannon.dao

import com.github.b1412.cannon.entity.Doc
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DocDao : JpaRepository<Doc, Long>


