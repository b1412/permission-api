package com.github.b1412.cannon.service

import com.github.b1412.cannon.dao.BranchDao
import com.github.b1412.cannon.entity.Branch
import com.github.b1412.cannon.service.base.BaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.persistence.EntityManager


@Service
class BranchService(
        @Autowired
        val branchDao: BranchDao,
        @Autowired
        val entityManager: EntityManager
) : BaseService<Branch, Long>(dao = branchDao)
