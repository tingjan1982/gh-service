package io.geekhub.service.user.repository

import io.geekhub.service.user.model.User
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*

interface UserRepository : PagingAndSortingRepository<User, String> {

    fun findByUsername(username: String): Optional<User>

    fun findByEmail(email: String): Optional<User>
}