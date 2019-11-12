package com.github.b1412.cannon.exceptions

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)!!


    @ExceptionHandler(value = [ResultNotFoundException::class])
    @Throws(Exception::class)
    fun resultNotFoundException(e: Exception): ResponseEntity<ApiResp> {
        val apiResp = ApiResp()
        apiResp.error = e.message
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResp)
    }

}