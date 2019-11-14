package com.github.b1412.permission.service.base


import com.github.b1412.permission.entity.Rule
import com.github.leon.aci.service.base.BaseService
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
class BaseRuleService : BaseService<Rule, Long>()

