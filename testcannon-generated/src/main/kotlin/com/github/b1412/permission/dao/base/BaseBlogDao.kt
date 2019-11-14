package com.github.b1412.permission.dao.base

import com.github.leon.aci.dao.base.BaseDao
import com.github.b1412.permission.entity.Blog
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface BaseBlogDao : BaseDao<Blog, Long>
