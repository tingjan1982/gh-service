package io.geekhub.service.user.web.bean

data class UserResponse(
        val id: Long,
        val username: String,
        val firstName: String,
        val lastName: String
)