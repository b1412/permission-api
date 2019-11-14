package com.github.b1412.permission.service.base


import com.github.b1412.permission.entity.Doc
import com.github.leon.aci.service.base.BaseService
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
class BaseDocService : BaseService<Doc, Long>()

