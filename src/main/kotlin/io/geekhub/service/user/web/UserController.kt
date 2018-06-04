package io.geekhub.service.user.web

import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.user.service.UserService
import io.geekhub.service.user.web.bean.UserRequest
import io.geekhub.service.user.web.bean.UserResponse
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/users")
class UserController(val userService: UserService) {

    @PostMapping
    fun createUser(@Valid @RequestBody userRequest: UserRequest): UserResponse {

        val createdUser = this.userService.createUser(userRequest)
        return createdUser.toDTO()
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: String): UserResponse {

        val user = this.userService.getUser(id)
        return user.toDTO()
    }

    @PostMapping("/{id}")
    fun updateUser(@PathVariable id: String, @RequestBody userRequest: UserRequest): UserResponse {

        val updatedUser = this.userService.updateUser(id, userRequest)
        return updatedUser.toDTO()
    }

    @PostMapping("/{id}/questions")
    fun addQuestionsToCollection(@PathVariable id: String, @RequestBody questions: List<String>): UserResponse {

        return this.userService.addSavedQuestion(id, questions).toDTO()
    }
}