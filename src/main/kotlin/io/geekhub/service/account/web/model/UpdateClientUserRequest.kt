package io.geekhub.service.account.web.model

import javax.validation.constraints.NotBlank

data class UpdateClientUserRequest(
        @field:NotBlank val name: String,
        @field:NotBlank val nickname: String
)