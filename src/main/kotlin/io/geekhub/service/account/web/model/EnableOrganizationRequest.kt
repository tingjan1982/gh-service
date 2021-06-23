package io.geekhub.service.account.web.model

import javax.validation.constraints.NotBlank

data class EnableOrganizationRequest(@field:NotBlank val organizationName: String)
