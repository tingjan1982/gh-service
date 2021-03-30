package io.geekhub.service.account.web.model

import javax.validation.constraints.Email

data class OrganizationUserRequest(@field:Email val email: String)
