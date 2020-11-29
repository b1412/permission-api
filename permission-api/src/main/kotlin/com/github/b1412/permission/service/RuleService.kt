package com.github.b1412.permission.service

import com.github.b1412.api.service.BaseService
import com.github.b1412.permission.dao.RuleDao
import com.github.b1412.permission.entity.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class RuleService(
        @Autowired
        val dao: RuleDao
) : BaseService<Rule, Long>(dao = dao)



