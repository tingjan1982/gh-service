package io.geekhub.service.shared.web

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.persistence.EntityExistsException
import javax.persistence.EntityNotFoundException

@ControllerAdvice
class ApiExceptionResolver : ResponseEntityExceptionHandler() {

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(exception: EntityNotFoundException): ResponseEntity<String> {

        return ResponseEntity.badRequest().body(exception.message)
    }

    @ExceptionHandler(EntityExistsException::class)
    fun handleEntityExists(exception: EntityExistsException): ResponseEntity<String> {

        return ResponseEntity.badRequest().body(exception.message)
    }
}