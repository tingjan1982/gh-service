package io.geekhub.service.user.repository

import io.geekhub.service.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, String> {

    fun findByUsername(username: String): Optional<User>
}