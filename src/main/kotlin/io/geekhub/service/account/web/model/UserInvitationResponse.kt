package io.geekhub.service.account.web.model

import io.geekhub.service.account.repository.ClientAccount

data class UserInvitationResponse(
    val inviterId: String,
    val inviterName: String,
    val inviterEmail: String,
    val inviterOrganization: String,
    val email: String,
    val status: ClientAccount.InvitationStatus)
