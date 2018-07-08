package io.geekhub.service.user.web.bean

import javax.validation.constraints.Size

data class UpdateUserRequest(
        val firstName: String?,
        val lastName: String?,
        @field:Size(min = 8, max = 50)
        val password: String)