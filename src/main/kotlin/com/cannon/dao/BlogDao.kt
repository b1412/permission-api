package com.cannon.dao

import com.cannon.bean.Blog
import com.cannon.dao.base.BaseDao
import org.springframework.stereotype.Repository

@Repository
interface BlogDao : BaseDao<Blog, Long>


