package io.geekhub.service.user.service

import io.geekhub.service.user.model.User

interface UserService {

    fun createUser(user: User): User

    fun updateUser(user: User): User
}