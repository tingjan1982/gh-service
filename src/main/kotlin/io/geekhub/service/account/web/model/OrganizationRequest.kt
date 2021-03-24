package io.geekhub.service.account.web.model

import javax.validation.constraints.NotBlank

data class OrganizationRequest(@NotBlank val name: String)
