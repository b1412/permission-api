package com.github.b1412.cannon.dao


import com.github.b1412.cannon.dao.base.BaseDao
import com.github.b1412.cannon.entity.Blog
import org.springframework.stereotype.Repository

@Repository
interface BlogDao : BaseDao<Blog, Long>


