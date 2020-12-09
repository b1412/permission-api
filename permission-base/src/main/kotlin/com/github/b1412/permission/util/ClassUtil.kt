package com.github.b1412.permission.util

import java.lang.reflect.Field
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OneToOne

object ClassUtil {
    fun firstLevelFields(type: Class<*>): List<Field> {
        val fields: MutableList<Field> = mutableListOf()
        val simpleFields = type.declaredFields.filter { field ->
            field.annotations.isEmpty() || field.annotations.all { annotation ->
                (annotation is OneToMany || annotation is OneToOne || annotation is ManyToOne || annotation is ManyToMany).not()
            }
        }
        fields.addAll(simpleFields)
        if (type.superclass != null) {
            fields.addAll(firstLevelFields(type.superclass))
        }
        return fields
    }

    fun Class<*>.allDeclaredFields(): List<Field> {
        val fields: MutableList<Field> = mutableListOf()
        fields.addAll(this.declaredFields)
        if (this.superclass != null) {
            fields.addAll(this.superclass.allDeclaredFields())
        }
        return fields
    }
}