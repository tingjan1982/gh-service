package io.geekhub.service.account.web.model

data class LightClientUserResponse(
    val id: String,
    val email: String,
    val name: String,
    val nickname: String?,
    val avatar: String?,
    val organization: ClientUserResponse.OrganizationResponse?
)
