package com.github.b1412.api.service

import arrow.core.*
import com.github.b1412.api.entity.BaseEntity
import com.github.b1412.api.dao.BaseDao
import com.github.b1412.extenstions.copyFrom
import org.joor.Reflect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.io.Serializable
import java.lang.reflect.Field
import javax.persistence.EntityManager
import javax.persistence.ManyToMany
import javax.persistence.OneToMany
import javax.persistence.OneToOne

@Component
abstract class BaseService<T, ID : Serializable>(
        private val dao: BaseDao<T, ID>
) : BaseDao<T, ID> by dao {

    @Autowired
    lateinit var securityFilter: SecurityFilter

    @Autowired
    lateinit var entityManager: EntityManager


    fun searchBySecurity(method: String, requestURI: String, params: Map<String, String>, pageable: Pageable): Page<T> {
        val securityFilters = securityFilter.query(method, requestURI)
        return dao.searchByFilter(params + securityFilters, pageable)
    }

    fun syncFromDb(baseEntity: BaseEntity) {
        val fields = baseEntity.javaClass.declaredFields.toList()
        fields.forEach { field ->
            val type = field.type
            val any = Reflect.on(baseEntity).get<Any>(field.name)
            if (BaseEntity::class.java.isAssignableFrom(field.type)) {
                val one2one = field.getAnnotation(OneToOne::class.java)
                if (one2one != null) {
                    Reflect.on(any).set(baseEntity::class.java.simpleName.toLowerCase(), baseEntity)
                } else {
                    when (val option = getObject(baseEntity, field, type)) {
                        is Some -> Reflect.on(baseEntity).set(field.name, option.t)
                    }
                }
            } else if (field.type.isAssignableFrom(MutableList::class.java)) {
                val one2one = field.getAnnotation(OneToMany::class.java)
                val many2Many = field.getAnnotation(ManyToMany::class.java)
                if (one2one != null) {
                    val list = baseEntity.toOption()
                            .flatMap { Reflect.on(it).get<Any>(field.name).toOption() }
                            .map { it as MutableList<out BaseEntity> }
                            .getOrElse { listOf() }
                            .map { obj ->
                                val id = Reflect.on(obj).get<Any>("id")
                                when (id) {
                                    null -> {
                                        if (one2one.mappedBy.isNotBlank()) {
                                            Reflect.on(obj).set(one2one.mappedBy, baseEntity)
                                        }
                                        syncFromDb(obj)
                                        obj
                                    }
                                    else -> {
                                        val oldNestedObj = entityManager.find(obj::class.java, id)
                                        val mergedObj = oldNestedObj.copyFrom(obj)
                                        if (one2one.mappedBy.isNotBlank()) {
                                            Reflect.on(mergedObj).set(one2one.mappedBy, baseEntity)
                                        }
                                        mergedObj
                                    }
                                }
                            }
                    Reflect.on(any).call("clear")
                    Reflect.on(any).call("addAll", list)
                } else if (many2Many != null) {
                    val list = baseEntity.toOption()
                            .flatMap { Reflect.on(it).get<Any>(field.name).toOption() }
                            .map { it as MutableList<out BaseEntity> }
                            .getOrElse { listOf() }
                            .map { obj ->
                                when (val id = Reflect.on(obj).get<Any>("id")) {
                                    null -> obj
                                    else -> entityManager.find(obj::class.java, id)
                                }
                            }
                    if (list.isNotEmpty()) {
                        Reflect.on(baseEntity).set(field.name, list)
                    }
                }
            }
        }
    }

    private fun getObject(baseEntity: BaseEntity, field: Field, type: Class<*>): Option<*> {
        return baseEntity.toOption()
                .flatMap { Reflect.on(baseEntity).get<Any>(field.name).toOption() }
                .flatMap { Reflect.on(it).get<Any>("id").toOption() }
                .map { entityManager.find(type, it) }
    }
}


