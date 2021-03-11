package io.geekhub.service.interview.web.model

import javax.validation.constraints.NotBlank

data class ChangeOwnerRequest(@NotBlank val userId: String)
