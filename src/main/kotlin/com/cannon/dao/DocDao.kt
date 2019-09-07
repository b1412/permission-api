package com.cannon.dao

import com.cannon.entity.Doc
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DocDao : JpaRepository<Doc, Long>


