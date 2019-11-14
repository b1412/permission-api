package com.github.b1412.permission.service.base


import com.github.b1412.permission.entity.Branch
import com.github.leon.aci.service.base.BaseService
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
class BaseBranchService : BaseService<Branch, Long>()

