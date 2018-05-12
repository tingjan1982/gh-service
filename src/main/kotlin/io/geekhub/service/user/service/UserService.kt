package io.geekhub.service.user.service

import io.geekhub.service.user.model.User
import io.geekhub.service.user.web.bean.UserRequest

interface UserService {

    fun createUser(userRequest: UserRequest): User

    fun createUser(user: User): User

    fun getUser(id: String): User

    fun updateUser(user: User): User

    fun updateUser(id: String, userRequest: UserRequest): User

}