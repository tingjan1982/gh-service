package io.geekhub.service.shared.web

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

/**
 * Reference:
 * https://www.toptal.com/java/spring-boot-rest-api-error-handling
 */
data class ApiError(
        val status: Int,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
        val timestamp: LocalDateTime = LocalDateTime.now(),
        val message: String,
        val subErrors: MutableList<ApiSubError> = mutableListOf(),
        var debugMessage: String?) {


    data class ApiSubError(
            val obj: String,
            val field: String,
            val rejectValue: String,
            val message: String
    )

}

