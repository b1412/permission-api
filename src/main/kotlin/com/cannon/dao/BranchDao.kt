package com.cannon.dao

import com.cannon.bean.Branch
import com.cannon.dao.base.BaseDao
import org.springframework.stereotype.Repository

@Repository
interface BranchDao : BaseDao<Branch, Long> {

}


