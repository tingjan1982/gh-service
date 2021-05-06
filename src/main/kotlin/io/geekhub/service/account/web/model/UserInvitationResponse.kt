package io.geekhub.service.account.web.model

data class UserInvitationResponse(
    val inviterId: String,
    val inviterName: String,
    val inviterEmail: String,
    val inviterOrganization: String,
    val email: String)
