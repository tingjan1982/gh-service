package io.geekhub.service.user.service

import io.geekhub.service.user.model.User
import io.geekhub.service.user.web.bean.UpdateUserRequest
import io.geekhub.service.user.web.bean.UserRequest
import java.util.*

interface UserService {

    fun createUser(userRequest: UserRequest): User

    fun getUser(id: String): User

    fun getUserByUsername(username: String): Optional<User>

    fun checkUserExists(id: String): Boolean

    fun updateUser(id: String, userRequest: UpdateUserRequest): User

    fun addSavedQuestion(id: String, questions: List<String>): User
}