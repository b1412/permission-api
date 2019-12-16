package com.github.b1412.cannon.dao

import com.github.b1412.cannon.dao.base.BaseDao
import com.github.b1412.cannon.entity.Doc
import org.springframework.stereotype.Repository

@Repository
interface DocDao : BaseDao<Doc, Long>
