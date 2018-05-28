package io.geekhub.service.shared.web

import io.geekhub.service.shared.exception.BusinessObjectNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.io.PrintWriter
import java.io.StringWriter
import java.time.LocalDateTime
import javax.persistence.EntityExistsException
import javax.persistence.EntityNotFoundException
import javax.servlet.http.HttpServletRequest
import javax.validation.ConstraintViolationException

@ControllerAdvice
class ApiExceptionResolver : ResponseEntityExceptionHandler() {

    @ExceptionHandler(EntityNotFoundException::class, BusinessObjectNotFoundException::class)
    fun handleEntityNotFound(request: HttpServletRequest, exception: Exception): ResponseEntity<ApiError> {

        return this.logError(HttpStatus.NOT_FOUND, exception, request)
    }

    @ExceptionHandler(EntityExistsException::class, ConstraintViolationException::class)
    fun `handle entity exists and Constraint error`(request: HttpServletRequest, exception: Exception): ResponseEntity<ApiError> {

        return this.logError(HttpStatus.BAD_REQUEST, exception, request)
    }

    /**
     * https://kotlinlang.org/docs/reference/lambdas.html
     */
    val logError = {
        httpStatus: HttpStatus, exception: Exception, request: HttpServletRequest ->
        val debugMessage = if (request.getParameter("debug") != null) {
            exception.stackTraceString
        } else {
            "Turn debug on to view"
        }

        ApiError(httpStatus.value(), LocalDateTime.now(), exception.message ?: "Not available", debugMessage).let {
            return@let ResponseEntity(it, httpStatus)
        }
    }
}

/**
 * https://stackoverflow.com/questions/1149703/how-can-i-convert-a-stack-trace-to-a-string
 * Look for Kotlin implementation
 */
val Exception.stackTraceString: String
    get() {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        this.printStackTrace(pw)
        return sw.toString()
    }