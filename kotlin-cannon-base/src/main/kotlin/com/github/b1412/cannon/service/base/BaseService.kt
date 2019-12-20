package com.github.b1412.cannon.service.base


import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import arrow.core.toOption
import com.github.b1412.cannon.dao.base.BaseDao
import com.github.b1412.cannon.entity.BaseEntity
import com.github.b1412.cannon.entity.User
import com.github.b1412.cannon.extenstions.copyFrom
import com.github.b1412.cannon.service.SecurityFilter
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


    fun searchBySecurity(method: String, requestURI: String, params: Map<String, String>,pageable: Pageable): Page<T> {
        val securityFilters = securityFilter.query(method, requestURI)
        return dao.searchByFilter(params + securityFilters,pageable)
    }


    fun syncSeleceOneFromDb(baseEntity: BaseEntity) {
        var fields = baseEntity.javaClass.declaredFields.toList()
        if (baseEntity.javaClass.superclass == User::class.java) {
            fields += listOf(*User::class.java.declaredFields)
        }
        fields.forEach { field ->
            val type = field.type
            val any = Reflect.on(baseEntity).get<Any>(field.name)
            if (BaseEntity::class.java.isAssignableFrom(field.type)) {
                val one2oneAnno = field.getAnnotation(OneToOne::class.java)
                if (one2oneAnno != null) {
                    Reflect.on(any).set(baseEntity::class.java.simpleName.toLowerCase(), baseEntity)
                } else {
                    val option = getObject(baseEntity, field, type)
                    when (option) {
                        is Some -> Reflect.on(baseEntity).set(field.name, option.t)
                    }
                }
            } else if (field.type.isAssignableFrom(MutableList::class.java)) {
                val oneToManyAnno = field.getAnnotation(OneToMany::class.java)
                val manyToManyAnno = field.getAnnotation(ManyToMany::class.java)
                if (oneToManyAnno != null && oneToManyAnno.orphanRemoval) {
                    val list = baseEntity.toOption()
                            .flatMap { it -> Reflect.on(it).get<Any>(field.name).toOption() }
                            .map { it as MutableList<out BaseEntity> }
                            .getOrElse { listOf<BaseEntity>() }
                            .map { obj ->
                                val id = Reflect.on(obj).get<Any>("id")
                                when (id) {
                                    null -> {
                                        // assign user,获取父entity 的user
                                        Reflect.on(obj).set(oneToManyAnno.mappedBy, baseEntity)
                                        //   entityManager.persist(obj)
                                        obj
                                    }
                                    else -> {
                                        val oldNestedObj = entityManager.find(obj::class.java, id)
                                        val mergedObj = oldNestedObj.copyFrom(obj)
                                        Reflect.on(mergedObj).set(oneToManyAnno.mappedBy, baseEntity)
                                        //  entityManager.merge(mergedObj)
                                        mergedObj
                                    }
                                }
                            }
                    Reflect.on(any).call("clear")
                    Reflect.on(any).call("addAll", list)
                } else if (manyToManyAnno != null) {
                    val list = baseEntity.toOption()
                            .flatMap { it -> Reflect.on(it).get<Any>(field.name).toOption() }
                            .map { it as MutableList<out BaseEntity> }
                            .getOrElse { listOf<BaseEntity>() }
                            .map { obj ->
                                val id = Reflect.on(obj).get<Any>("id")
                                when (id) {
                                    null -> obj
                                    else -> entityManager.find(obj::class.java, id)
                                }
                            }
                    if (!list.isEmpty()) {
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


