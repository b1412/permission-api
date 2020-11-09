package com.github.b1412.permission.dao

import com.github.b1412.api.dao.BaseDao
import com.github.b1412.permission.entity.Rule
import org.springframework.stereotype.Repository

@Repository
interface RuleDao : BaseDao<Rule, Long>
