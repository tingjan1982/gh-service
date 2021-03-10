package io.geekhub.service.account.web.model

import javax.validation.constraints.Email

data class OrganizationUserRequest(@Email val email: String)
