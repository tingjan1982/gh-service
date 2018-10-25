package io.geekhub.service.questions.service

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.fail
import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.model.QuestionAttribute.Companion.TOTAL_LIKES_KEY
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.user.model.User
import io.geekhub.service.user.repository.UserRepository
import io.geekhub.service.user.service.UserService
import io.geekhub.service.user.web.bean.UserRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.context.support.WithUserDetails
import java.util.concurrent.atomic.AtomicInteger

@IntegrationTest
internal class SocialLikeServiceImplIntegrationTest {

    @Autowired
    lateinit var socialLikeService: SocialLikeServiceImpl

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var questionService: QuestionService

    lateinit var user: User

    lateinit var question: Question

    @BeforeEach
    fun prepare() {
        user = userService.createUser(UserRequest("test-user", "test", "user", "email", "password"))
        question = questionService.saveQuestion(Question(question = "test question"))
        
    }

    /**
     * WithMockUser specifies the user that exists in the security context and
     * WithUserDetails further specifies the username that is used to look up the UserDetails for subsequent authorization check
     * such as method security annotation.
     */
    @Test
    @WithMockUser(roles = ["ADMIN"])
    @WithUserDetails("test-user")
    fun likeQuestion() {

        socialLikeService.likeQuestion(question.questionId.toString(), user.id.toString())
        userRepository.getOne(user.id.toString()).let {
            assert(it.likedQuestions.size).isEqualTo(1)
        }

        socialLikeService.likeCounts.getOrDefault(question.questionId.toString(), AtomicInteger(0)).let {
            assert(it.get()).isEqualTo(1)
        }

        socialLikeService.saveLikedQuestionsPeriodically()

        questionService.getQuestionAttribute(question.questionId.toString(), TOTAL_LIKES_KEY)?.let {
            assert(it.value.toInt()).isEqualTo(1)
        } ?: fail("attribute is not found")

    }
}