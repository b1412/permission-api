package com.cannon.dao

import com.cannon.bean.Doc
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DocDao : JpaRepository<Doc, Long> {

}


