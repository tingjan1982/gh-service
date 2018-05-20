package io.geekhub.service.user.web.bean

data class UserRequest(
        val username: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val password: String
)