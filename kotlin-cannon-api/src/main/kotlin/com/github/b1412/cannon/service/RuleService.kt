package com.github.b1412.cannon.service

import com.github.b1412.cannon.dao.RuleDao
import com.github.b1412.cannon.entity.Rule
import com.github.b1412.cannon.service.base.BaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class RuleService(
        @Autowired
        val dao: RuleDao
) : BaseService<Rule, Long>(dao = dao)



