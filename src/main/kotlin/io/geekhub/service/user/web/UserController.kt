package io.geekhub.service.user.web

import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.user.service.UserService
import io.geekhub.service.user.web.bean.UserRequest
import io.geekhub.service.user.web.bean.UserResponse
import org.apache.commons.lang3.math.NumberUtils
import org.springframework.web.bind.annotation.*
import javax.persistence.EntityNotFoundException

@RestController
@RequestMapping("/users")
class UserController(val userService: UserService) {

    @PostMapping
    fun createUser(@RequestBody userRequest: UserRequest): UserResponse {

        val createdUser = this.userService.createUser(userRequest)
        return createdUser.toDTO()
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: String): UserResponse {

        // TODO: unify the id to long handling, perhaps through extension functions
        if (NumberUtils.isDigits(id)) {
            val user = this.userService.getUser(id.toLong())
            return user.toDTO()
        }

        throw EntityNotFoundException("User cannot be found: $id")
    }

    @PostMapping("/{id}")
    fun updateUser(@PathVariable id: String, @RequestBody userRequest: UserRequest): UserResponse {

        val updatedUser = this.userService.updateUser(id.toLong(), userRequest)
        return updatedUser.toDTO()
    }
}