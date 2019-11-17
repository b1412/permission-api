package com.github.b1412.cannon.service.base


import com.github.b1412.cannon.dao.base.BaseDao
import com.github.b1412.cannon.service.rule.SecurityFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.io.Serializable

@Component
abstract class BaseService<T, ID : Serializable> {
    @Autowired
    lateinit var baseDao: BaseDao<T, ID>

    @Autowired
    lateinit var securityFilter: SecurityFilter


    fun findByFilter(filter: Map<String, String>): List<T> {
        return baseDao.searchByFilter(filter)
    }

    fun findBySecurity(method: String, requestURI: String, params: Map<String, String>, pageable: Pageable): List<T> {
        val securityFilters = securityFilter.query(method, requestURI)
        return baseDao.searchByFilter(params + securityFilters)
    }


}


