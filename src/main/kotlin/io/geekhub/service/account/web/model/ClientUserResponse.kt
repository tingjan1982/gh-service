package io.geekhub.service.account.web.model

import io.geekhub.service.account.repository.ClientUser

data class ClientUserResponse(
        val id: String,
        val email: String,
        val name: String,
        val nickname: String,
        val avatar: String?,
        val userType: ClientUser.UserType,
        val accountPrivilege: ClientUser.AccountPrivilege,
        val organization: ClientOrganizationResponse?,
        val metadata: Map<String, Any>? = mapOf(),
        val invitations: List<UserInvitationResponse>
)