package io.geekhub.service.account.web.model

import javax.validation.constraints.NotBlank

data class EnableOrganizationRequest(@NotBlank val organizationName: String)
