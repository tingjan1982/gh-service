package io.geekhub.service.likes.service

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.service.InterviewService
import io.geekhub.service.likes.data.LikeRecordRepository
import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.service.QuestionService
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.shared.extensions.DummyObject
import io.geekhub.service.specialization.repository.Specialization
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser

@IntegrationTest
//@TestPropertySource(properties = ["spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration"])
internal class LikeServiceImplTest {

    @Autowired
    lateinit var likeService: LikeService

    @Autowired
    lateinit var questionService: QuestionService

    @Autowired
    lateinit var interviewService: InterviewService

    @Autowired
    lateinit var likeRecordRepository: LikeRecordRepository

    @Autowired
    lateinit var clientAccount: ClientAccount

    @Autowired
    lateinit var specialization: Specialization

    @Test
    @WithMockUser
    fun likeQuestion() {

        DummyObject.dummyQuestion(clientAccount).let {
            questionService.saveQuestion(it)

        }.let {
            likeService.like(clientAccount, it).run {
                assertThat(this.likedClientAccount).isEqualTo(clientAccount.id)
                assertThat(this.objectId).isEqualTo(it.id)
                assertThat(this.objectType).isEqualTo(Question::class.toString())
            }

            likeService.like(clientAccount, it) // this should have no effect as it has already been liked.

            questionService.getQuestion(it.id.toString()).run {
                assertThat(this.likeCount).isEqualTo(1)
            }

            it
        }.let {
            likeService.unlike(clientAccount, it)
            likeService.unlike(clientAccount, it) // this should have no effect as it has already been unliked

            questionService.getQuestion(it.id.toString()).run {
                assertThat(this.likeCount).isEqualTo(0)
            }

            assertThat(likeRecordRepository.findAll().toHashSet()).isEmpty()
        }
    }

    @Test
    @WithMockUser
    fun likeInterview() {

        DummyObject.dummyInterview(clientAccount, specialization).let {
            interviewService.saveInterview(it)

        }.let {
            likeService.like(clientAccount, it).run {
                assertThat(this.likedClientAccount).isEqualTo(clientAccount.id)
                assertThat(this.objectId).isEqualTo(it.id)
                assertThat(this.objectType).isEqualTo(Interview::class.toString())
            }

            likeService.like(clientAccount, it) // this should have no effect as it has already been liked.

            interviewService.getInterview(it.id.toString()).run {
                assertThat(this.likeCount).isEqualTo(1)
            }

            it
        }.let {
            likeService.unlike(clientAccount, it)
            likeService.unlike(clientAccount, it) // this should have no effect as it has already been unliked

            interviewService.getInterview(it.id.toString()).run {
                assertThat(this.likeCount).isEqualTo(0)
            }

            assertThat(likeRecordRepository.findAll().toHashSet()).isEmpty()
        }
    }
}