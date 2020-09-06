package com.github.b1412.cannon.extenstions

import arrow.core.getOrElse
import arrow.core.toOption
import arrow.syntax.collections.tail
import com.github.b1412.api.entity.BaseEntity
import org.joor.Reflect
import org.springframework.http.ResponseEntity
import java.math.BigDecimal
import kotlin.reflect.full.memberFunctions

fun <T> T?.responseEntityOk(): ResponseEntity<T> {
    return ResponseEntity.ok(this!!)
}

fun <T> T?.responseEntityBadRequest(): ResponseEntity<T> {
    return ResponseEntity.badRequest().body(this)
}

fun Any?.println() {
    println(this)
}

fun Any?.print() {
    print(this)
}

fun <T> T?.orElse(default: T): T {
    return this.toOption().getOrElse { default }
}

fun <T> Any.copyFrom(newObj: T): T {

    if (newObj == null) {
        throw  IllegalArgumentException("from object is null")
    }
    if (newObj !is BaseEntity) {
        throw  IllegalArgumentException("from Class is not  extended from BaseEntity")
    }
    val method = this::class.memberFunctions.first { it.name == "copy" }

//    if (methodTry.isFailure()) {
//        throw  IllegalArgumentException("from Class ${this::class.java.name} is not a data class")
//    }
    val parameterNames = method
            .parameters
            .tail()
            .mapIndexed { index, kParameter -> Pair(index, kParameter.name) }
    val oldValues = parameterNames.map {
        val v = Reflect.on(this).get<Any?>(it.second)
        v
    }

    val newValues = parameterNames.map {
        Reflect.on(newObj).get<Any?>(it.second)
    }
    val mergedValues = oldValues.zip(newValues).map { it.second.orElse(it.first) }
    val payerNew = method.call(this, *mergedValues.toTypedArray())
    val fields = BaseEntity::class.java.declaredFields
    fields.filter { it.name != "serialVersionUID" && it.name != "Companion" }
            .forEach {
                val oldV = Reflect.on(this).get<Any>(it.name)
                Reflect.on(payerNew).set(it.name, oldV)
            }
    return payerNew as T
}


fun <T> Iterable<T>.sumByBigDecimal(selector: (T) -> BigDecimal): BigDecimal {
    return this.map { selector(it) }.fold(BigDecimal.ZERO, BigDecimal::add)
}



