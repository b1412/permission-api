package com.cannon.dao

import com.cannon.entity.Blog
import com.cannon.dao.base.BaseDao
import org.springframework.stereotype.Repository

@Repository
interface BlogDao : BaseDao<Blog, Long>


