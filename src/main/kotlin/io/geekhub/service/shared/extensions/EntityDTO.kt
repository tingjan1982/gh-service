package io.geekhub.service.shared.extensions

import io.geekhub.service.user.model.User
import io.geekhub.service.user.web.bean.UserRequest
import io.geekhub.service.user.web.bean.UserResponse

fun UserRequest.toEntity() = User(
        username = this.username,
        firstName = this.firstName,
        lastName = this.lastName
)

fun User.toDTO() = UserResponse(
        this.id,
        this.username,
        this.firstName,
        this.lastName
)