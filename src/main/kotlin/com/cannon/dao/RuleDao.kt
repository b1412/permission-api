package com.cannon.dao


import com.cannon.entity.Rule
import com.cannon.dao.base.BaseDao
import org.springframework.stereotype.Repository

@Repository
interface RuleDao : BaseDao<Rule, Long>