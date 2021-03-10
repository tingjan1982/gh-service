package io.geekhub.service.account.web.model

data class UserInvitationResponse(
    val inviterId: String,
    val inviterName: String,
    val inviterEmail: String,
    val email: String)
