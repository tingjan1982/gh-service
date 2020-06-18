package io.geekhub.service.account.web.model

data class UpdateUserPasswordRequest(
        val oldPassword: String,
        val newPassword: String
) {

    lateinit var userId: String
    lateinit var email: String
}
