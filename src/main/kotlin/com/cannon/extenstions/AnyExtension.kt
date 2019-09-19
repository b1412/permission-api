package com.cannon.extenstions


import org.springframework.http.ResponseEntity

fun <T> T?.responseEntityOk(): ResponseEntity<T> {
    return ResponseEntity.ok(this!!)
}

fun <T> T?.responseEntityBadRequest(): ResponseEntity<T> {
    return ResponseEntity.badRequest().body(this)
}





