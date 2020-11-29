package com.github.b1412.permission.service

import com.github.b1412.api.service.BaseService
import com.github.b1412.permission.dao.BranchDao
import com.github.b1412.permission.entity.Branch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class BranchService(
        @Autowired
        val branchDao: BranchDao
) : BaseService<Branch, Long>(dao = branchDao)
