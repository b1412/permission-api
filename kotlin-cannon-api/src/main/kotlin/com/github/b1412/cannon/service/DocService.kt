package com.github.b1412.cannon.service

import com.github.b1412.cannon.service.base.BaseService
import com.github.b1412.cannon.dao.DocDao
import com.github.b1412.cannon.entity.Doc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class DocService(
    @Autowired
    val dao: DocDao
) : BaseService<Doc, Long>(dao = dao)



