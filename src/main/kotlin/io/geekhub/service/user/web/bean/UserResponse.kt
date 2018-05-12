package io.geekhub.service.user.web.bean

data class UserResponse(
        val id: String,
        val username: String,
        val firstName: String,
        val lastName: String,
        val email: String
)