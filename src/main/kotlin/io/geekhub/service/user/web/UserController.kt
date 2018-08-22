package io.geekhub.service.user.web

import io.geekhub.service.shared.exception.UnexpectedAuthException
import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.user.model.User
import io.geekhub.service.user.service.UserService
import io.geekhub.service.user.web.bean.UpdateUserRequest
import io.geekhub.service.user.web.bean.UserRequest
import io.geekhub.service.user.web.bean.UserResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails
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

    @GetMapping("/me")
    fun getCurrentUser(): UserResponse {

        val oauth2Authentication = SecurityContextHolder.getContext().authentication as OAuth2Authentication

        val userDetails = oauth2Authentication.details as? OAuth2AuthenticationDetails
                ?: throw UnexpectedAuthException("get current user information.")

        return (userDetails.decodedDetails as User).toDTO()
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: String): UserResponse {

        val user = this.userService.getUser(id)
        return user.toDTO()
    }

    @PostMapping("/{id}")
    fun updateUser(@PathVariable id: String, @Valid @RequestBody userRequest: UpdateUserRequest): UserResponse {

        val updatedUser = this.userService.updateUser(id, userRequest)
        return updatedUser.toDTO()
    }

    @PostMapping("/{id}/questions")
    fun addQuestionsToCollection(@PathVariable id: String, @RequestBody questions: List<String>): UserResponse {

        return this.userService.addSavedQuestion(id, questions).toDTO()
    }
}