package io.geekhub.service.user.service

import io.geekhub.service.user.model.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UserServiceImplIntegrationTest {

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun createUser() {
        val createdUser = this.userService.createUser(User(username = "joelin"))

        assertNotNull(createdUser.id)
    }

    @Test
    fun updateUser() {
        val createdUser = this.userService.createUser(User(username = "joelin", firstName = "Joe", lastName = "Lin"))
        createdUser.lastName = "Changed"
        val updatedUser = this.userService.updateUser(createdUser)

        assertEquals("Changed", updatedUser.lastName)
    }
}