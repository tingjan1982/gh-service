package io.geekhub.service.account.web.model

import javax.validation.constraints.NotBlank

data class JoinOrganizationRequest(@NotBlank val organizationId: String)
