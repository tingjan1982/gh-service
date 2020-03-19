package io.geekhub.service.specialization.web.model

import javax.validation.constraints.NotEmpty

data class SpecializationRequest(
        @field:NotEmpty val name: String,
        val profession: String?
)
