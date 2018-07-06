package io.geekhub.service.user.service

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.service.QuestionService
import io.geekhub.service.user.model.User
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
internal class UserServiceImplIntegrationTest {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var questionService: QuestionService

    private lateinit var createdUser: User

    @BeforeEach
    fun prepare() {
        this.createdUser = this.userService.createUser(User(username = "joelin", firstName = "Joe", lastName = "Lin", email = "tingjan1982@gmail.com"))

    }

    @Test
    fun createUser() {
        assertNotNull(this.createdUser.id)
        assert(this.createdUser.lastModifiedDate.get()).isEqualTo(this.createdUser.createdDate.get())
    }

    @Test
    fun updateUser() {
        this.createdUser.lastName = "Changed"

        this.userService.updateUser(createdUser).let {
            assert(it.lastName).isEqualTo("Changed")
            assert(it.lastModifiedDate.get()).isNotEqualTo(it.createdDate.get())
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