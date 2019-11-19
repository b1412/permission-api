package com.github.b1412.cannon.service.base


import com.github.b1412.cannon.dao.base.BaseDao
import com.github.b1412.cannon.service.SecurityFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.Serializable

@Component
abstract class BaseService<T, ID : Serializable>(
        private val dao: BaseDao<T, ID>
) : BaseDao<T, ID> by dao {

    @Autowired
    lateinit var securityFilter: SecurityFilter


    fun searchBySecurity(method: String, requestURI: String, params: Map<String, String>): List<T> {
        val securityFilters = securityFilter.query(method, requestURI)
        return dao.searchByFilter(params + securityFilters)
    }


}


