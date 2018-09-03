package io.geekhub.service.user.service

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.service.QuestionService
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.user.model.User
import io.geekhub.service.user.web.bean.UpdateUserRequest
import io.geekhub.service.user.web.bean.UserRequest
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

@IntegrationTest
internal class UserServiceImplIntegrationTest {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var questionService: QuestionService

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    private lateinit var createdUser: User

    @BeforeAll
    fun prepare() {
        SecurityContextHolder.getContext().authentication = TestingAuthenticationToken("admin", "admin", "ROLE_ADMIN").also {
            it.isAuthenticated = true
        }
        
        this.createdUser = this.userService.createUser(UserRequest(username = "joelin",
                firstName = "Joe",
                lastName = "Lin",
                email = "tingjan1982@gmail.com",
                password = "password"))

    }

    @Test
    fun createUser() {
        assertNotNull(this.createdUser.id)
        assert(this.createdUser.lastModifiedDate.get()).isEqualTo(this.createdUser.createdDate.get())
    }

    @Test
    fun updateUser() {

        this.userService.updateUser(this.createdUser.id.toString(), UpdateUserRequest(
                firstName = "changed",
                lastName = "changed",
                password = "changeit"
        )).let {
            assert(it.firstName).isEqualTo("changed")
            assert(it.lastName).isEqualTo("changed")
            assert(it.lastModifiedDate.get()).isNotEqualTo(it.createdDate.get())
        }

        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(this.createdUser.username, "changeit")).let {
            assertTrue(it.isAuthenticated)
        }
    }

    @Test
    fun createUserWithSavedQuestions() {

        val createdQuestion = this.questionService.saveQuestion(Question("sample question"))

        val user = this.userService.addSavedQuestion(this.createdUser.userId.toString(),
                listOf(createdQuestion.questionId.toString()))

        assert(user.savedQuestions.size).isEqualTo(1)
    }
}