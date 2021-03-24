package io.geekhub.service.account.web.model

import io.geekhub.service.account.repository.ClientAccount

data class ClientOrganizationResponse(
    val id: String,
    val name: String,
    val userInvitations: Set<ClientAccount.UserInvitation>,
    val users: List<LightClientUserResponse>
)
