package io.geekhub.service.shared.exception

import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import java.util.*

/**
 * Wraps MethodArgumentNotValidException to override the error message returned to API caller
 * to hide implementation details.
 *
 * https://stackoverflow.com/questions/33663801/how-do-i-customize-default-error-message-from-spring-valid-validation
 * http://www.springboottutorial.com/spring-boot-validation-for-rest-services
 */
class FriendlyMethodArgumentNotValidException(private val exception: MethodArgumentNotValidException) : Exception() {

    val fieldErrors: List<FieldError> = exception.bindingResult.fieldErrors

    override val message: String?
        get() {
            val fieldErrorString = StringJoiner(", ")

            for (error in this.fieldErrors) {
                fieldErrorString.add("[field=${error.field}, rejected value=${error.rejectedValue}, ${error.defaultMessage}]")
            }

            return "Validation failed, containing (${this.exception.bindingResult.errorCount}) errors: $fieldErrorString"
        }
}