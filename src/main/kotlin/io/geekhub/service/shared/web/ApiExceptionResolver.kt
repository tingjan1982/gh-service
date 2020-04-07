package io.geekhub.service.shared.web

import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.exception.BusinessObjectNotFoundException
import io.geekhub.service.shared.exception.FriendlyMethodArgumentNotValidException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.io.PrintWriter
import java.io.StringWriter
import javax.persistence.EntityExistsException
import javax.persistence.EntityNotFoundException
import javax.servlet.http.HttpServletRequest
import javax.validation.ConstraintViolationException

typealias SpringAccessDeniedException = org.springframework.security.access.AccessDeniedException

/**
 * RestControllerAdvice saves user from having to specify @ResponseBody,
 * similar to what RestController does.
 *
 * TODO: try using @ResponseStatus to specify http status.
 */
@RestControllerAdvice
class ApiExceptionResolver : ResponseEntityExceptionHandler() {

    @ExceptionHandler(EntityNotFoundException::class, BusinessObjectNotFoundException::class)
    fun handleEntityNotFound(request: HttpServletRequest, exception: Exception): ResponseEntity<ApiError> {

        return this.logError(HttpStatus.NOT_FOUND, exception, request)
    }

    @ExceptionHandler(SpringAccessDeniedException::class)
    fun handleAccessDeniedException(request: HttpServletRequest, exception: SpringAccessDeniedException): ResponseEntity<ApiError> {

        val errorMsg = "${exception.message} - your current credentials may not allow access to requested resource."
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiError(status = HttpStatus.FORBIDDEN.value(), message = errorMsg))
    }

    @ExceptionHandler(EntityExistsException::class, ConstraintViolationException::class)
    fun handlePersistenceRelatedExceptions(request: HttpServletRequest, exception: Exception): ResponseEntity<ApiError> {

        return this.logError(HttpStatus.BAD_REQUEST, exception, request)
    }

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(request: HttpServletRequest, exception: Exception): ResponseEntity<ApiError> {

        return this.logError(HttpStatus.PRECONDITION_FAILED, exception, request)
    }

    /**
     * 422 is recommended by a couple of post:
     * http://parker0phil.com/2014/10/16/REST_http_4xx_status_codes_syntax_and_sematics/
     * http://www.restapitutorial.com/httpstatuscodes.html
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleValidationException(request: HttpServletRequest, exception: Exception): ResponseEntity<ApiError> {

        return this.logError(HttpStatus.UNPROCESSABLE_ENTITY, exception, request)
    }

    /**
     * https://stackoverflow.com/questions/33663801/how-do-i-customize-default-error-message-from-spring-valid-validation
     */
    override fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {

        val friendlyException = FriendlyMethodArgumentNotValidException(ex)
        val apiError = this.constructApiError(HttpStatus.UNPROCESSABLE_ENTITY, friendlyException, request.getParameter("debug") != null)

        friendlyException.fieldErrors.forEach {
            apiError.subErrors.add(ApiError.ApiSubError(it.objectName, it.field, it.rejectedValue.toString(), it.defaultMessage.toString()))
        }

        return this.handleExceptionInternal(ex, apiError, headers, status, request)
    }

    /**
     * https://kotlinlang.org/docs/reference/lambdas.html
     */
    val logError = { httpStatus: HttpStatus, exception: Exception, request: HttpServletRequest ->

        constructApiError(httpStatus, exception, request.getParameter("debug") != null).let {
            return@let ResponseEntity(it, httpStatus)
        }
    }

    val constructApiError = { httpStatus: HttpStatus, exception: Exception, debug: Boolean ->
        val debugMessage = if (debug) {
            exception.stackTraceString
        } else {
            "Turn debug on to view"
        }

        ApiError(status = httpStatus.value(), message = exception.message ?: "Not available", debugMessage = debugMessage)
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