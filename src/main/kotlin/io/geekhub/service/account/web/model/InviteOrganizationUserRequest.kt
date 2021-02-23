package io.geekhub.service.account.web.model

import javax.validation.constraints.Email

data class InviteOrganizationUserRequest(@Email val email: String)
