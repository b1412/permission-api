package com.github.b1412.util

import com.github.b1412.api.entity.BaseEntity
import com.github.b1412.permission.util.ClassUtil
import org.junit.jupiter.api.Test
import javax.persistence.OneToMany


class BaseComponent : BaseEntity() {
    override var id: Long? = null
    val label: String? = null
    val fieldId: String? = null
    val cssClassName: String? = null
    var required: Boolean? = false
    val sort: Int? = null

    @OneToMany
    val list: MutableList<BaseComponent> = mutableListOf()
}

class ClassUtilTest {


    @Test
    fun getLinkedListFields() {
        ClassUtil.firstLevelFields(BaseComponent::class.java).forEach {
            println(it)
        }
    }
}