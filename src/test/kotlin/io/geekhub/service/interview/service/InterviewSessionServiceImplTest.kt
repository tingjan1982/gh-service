package io.geekhub.service.interview.service

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.specialization.repository.Specialization
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import java.time.Instant
import java.util.*

@IntegrationTest
internal class InterviewSessionServiceImplTest {

    @Autowired
    lateinit var interviewSessionService: InterviewSessionService

    @Autowired
    lateinit var interviewService: InterviewService

    @Autowired
    lateinit var clientAccount: ClientAccount

    @Autowired
    lateinit var specialization: Specialization

    lateinit var interview: Interview

    @BeforeEach
    fun prepareInterview() {
        Interview(title = "dummy interview", jobTitle = "Engineer", clientAccount = clientAccount, specialization = specialization).let {
            interview = interviewService.saveInterview(it)
        }
    }

    @Test
    @WithMockUser
    fun saveInterviewSession() {

        InterviewSession(
                interview = interview,
                clientAccount = interview.clientAccount,
                userEmail = "joelin@geekhub.tw",
                name = "Joe Lin",
                interviewMode = InterviewSession.InterviewMode.REAL,
                duration = 1
        ).let { it ->
            interviewSessionService.saveInterviewSession(it)

        }.let {
            interviewSessionService.startInterviewSession(it).also { session ->
                assertThat(session.interviewStartDate).isNotNull()
            }
        }.let {
            interviewSessionService.addAnswerAttempt(it, "question-id", InterviewSession.QuestionAnswerAttempt(answerId = "answer-id")).run {
                assertThat(this.id).isNotNull()
                assertThat(this.answerAttempts).hasSize(1)
            }

            interviewSessionService.getInterviewSession(it.id.toString()).run {
                assertThat(this).all {
                    isNotNull()
                    isEqualTo(it)
                }
            }

            return@let it
        }.let {
            assertThat {
                interviewSessionService.startInterviewSession(it)
            }.isFailure().isInstanceOf(BusinessException::class)

            return@let it
        }.let {
            interviewSessionService.submitInterviewSession(it).run {
                assertThat(this.interviewEndDate).isNotNull()
            }
        }
    }

    @Test
    @WithMockUser
    fun `verify failed add answer attempts`() {

        InterviewSession(
                interview = interview,
                clientAccount = interview.clientAccount,
                userEmail = "joelin@geekhub.tw",
                name = "Joe Lin",
                interviewMode = InterviewSession.InterviewMode.REAL,
                duration = 1
        ).let { it ->
            interviewSessionService.saveInterviewSession(it)
        }.let {
            assertThat {
                interviewSessionService.addAnswerAttempt(it, "dummy", InterviewSession.QuestionAnswerAttempt(answerId = "a-id"))
            }.isFailure().isInstanceOf(BusinessException::class)

            return@let it
        }.let {
            it.interviewStartDate = Date.from(Instant.now().minusSeconds(300))
            interviewSessionService.saveInterviewSession(it)
        }.let {
            assertThat {
                interviewSessionService.addAnswerAttempt(it, "dummy", InterviewSession.QuestionAnswerAttempt(answerId = "a-id"))
            }.isFailure().isInstanceOf(BusinessException::class)
        }
    }
}