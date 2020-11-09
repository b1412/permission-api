package com.github.b1412.permission.dao

import com.github.b1412.api.dao.BaseDao
import com.github.b1412.permission.entity.Branch
import org.springframework.stereotype.Repository

@Repository
interface BranchDao : BaseDao<Branch, Long>
