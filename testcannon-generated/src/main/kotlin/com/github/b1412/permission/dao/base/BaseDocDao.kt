package com.github.b1412.permission.dao.base

import com.github.leon.aci.dao.base.BaseDao
import com.github.b1412.permission.entity.Doc
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface BaseDocDao : BaseDao<Doc, Long>
