package io.geekhub.service.user.web.bean

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

/**
 * https://stackoverflow.com/questions/35847763/kotlin-data-class-bean-validation-jsr-303
 */
data class UserRequest(
        @field:Size(min = 6, max = 50)
        val username: String,
        @field:NotEmpty
        val firstName: String,
        @field:NotEmpty
        val lastName: String,
        @field:Email
        val email: String,
        @field:Size(min = 8, max = 50)
        val password: String
)