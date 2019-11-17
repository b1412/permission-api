package com.github.b1412.cannon.dao

import com.github.b1412.cannon.dao.base.BaseDao
import com.github.b1412.cannon.entity.Branch
import org.springframework.stereotype.Repository

@Repository
interface BranchDao : BaseDao<Branch, Long>


