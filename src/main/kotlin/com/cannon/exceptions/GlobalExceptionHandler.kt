package com.cannon.exceptions

import com.cannon.extenstions.responseEntityBadRequest
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.validation.ConstraintViolationException

@ControllerAdvice
class GlobalExceptionHandler {
    val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)!!

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun methodArgumentNotValid(req: HttpServletRequest, e: Exception): ResponseEntity<ApiResp> {
        val exception = e as MethodArgumentNotValidException
        val errorMsg = exception.bindingResult.fieldErrors
                .map { "${it.field}-${it.rejectedValue}-${it.defaultMessage}" }
                .joinToString("|")
        val apiResp = ApiResp()
        apiResp.error = errorMsg
        return apiResp.responseEntityBadRequest()
    }

    @ExceptionHandler(value = [ConstraintViolationException::class])
    fun constraintViolationExceptionHandler(req: HttpServletRequest, e: Exception): ResponseEntity<*> {
        val rootCause = e as ConstraintViolationException

        val apiResp = ApiResp()
        val message: String
        message = rootCause.constraintViolations
                .map { it.propertyPath + " " + it.message + ", but the actual value is " + it.invalidValue }
                .joinToString(";")
        apiResp.error = message
        return apiResp.responseEntityBadRequest()
    }

    @ExceptionHandler(value = [AccessDeniedException::class) )
    @Throws(Exception::class)
    fun noPermission(req: HttpServletRequest, e: Exception): ResponseEntity<ApiResp> {
        val apiResp = ApiResp()
        apiResp.error = e.message
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResp)
    }

    @ExceptionHandler(value = [HttpRequestMethodNotSupportedException::class])
    @Throws(Exception::class)
    fun methodNotSupported(req: HttpServletRequest, e: Exception): ResponseEntity<ApiResp> {
        val apiResp = ApiResp()
        apiResp.error = "method " + req.method + " ,url " + req.requestURI
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResp)
    }


    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    @Throws(Exception::class)
    fun httpMessageNotReadableException(req: HttpServletRequest, e: Exception): ResponseEntity<ApiResp> {
        val apiResp = ApiResp()
        apiResp.error = e.message
        return apiResp.responseEntityBadRequest()
    }


    @ExceptionHandler(value = [IllegalArgumentException::class])
    @Throws(Exception::class)
    fun illegalArgumentException(e: Exception): ResponseEntity<ApiResp> {
        // log.error(e.getMessage(), e);
        val apiResp = ApiResp()
        apiResp.error = e.message
        return apiResp.responseEntityBadRequest()
    }


    @ExceptionHandler(value = [ResultNotFoundException::class])
    @Throws(Exception::class)
    fun resultNotFoundException(e: Exception): ResponseEntity<ApiResp> {
        // log.error(e.getMessage(), e);
        val apiResp = ApiResp()
        apiResp.error = e.message
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResp)
    }

    @ExceptionHandler(value = [MethodArgumentTypeMismatchException::class])
    fun methodArgumentTypeMismatchException(e: Exception): ResponseEntity<ApiResp> {
        val apiResp = ApiResp()
        apiResp.error = e.message
        return apiResp.responseEntityBadRequest()
    }


    @ExceptionHandler(value = [DataIntegrityViolationException::class])
    @Throws(Exception::class)
    fun sqlErrorHandler(req: HttpServletRequest, e: Exception): ResponseEntity<ApiResp> {
        val rootCause = (e as DataIntegrityViolationException).rootCause!!
        var message = rootCause.message!!
        val mav = ModelAndView()
        mav.addObject("exception", e)
        if (message.contains("Duplicate entry")) {
            message = StringUtils.substringBetween(message, "Duplicate entry", " for key")
            message = "Duplicate data: $message"
        } else {
            // FK
            val db = "db name"
            message = StringUtils.substringBetween(message, "a foreign key constraint fails (`$db`.`", "`, CONSTRAINT")
            message = "It's used by a $message"

        }
        return ApiResp(error = message, message = e.message).responseEntityBadRequest()
    }

    @ExceptionHandler(value = [Exception::class])
    fun defaultErrorHandler(req: HttpServletRequest, e: Exception): ResponseEntity<ApiResp> {
        log.error("unknown error ", e)
        val apiResp = ApiResp()
        apiResp.error = e.message
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResp)
    }
}