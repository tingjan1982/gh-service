package io.geekhub.service.interview.service

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.model.PublishedInterview
import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.specialization.repository.Specialization
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import java.math.BigDecimal
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

    lateinit var publishedInterview: PublishedInterview

    @BeforeEach
    fun prepareInterview() {
        Interview(title = "dummy interview", jobTitle = "Engineer", clientAccount = clientAccount, specialization = specialization).apply {
            Interview.Section(title = "default").apply {
                this.questions.add(
                        Interview.QuestionSnapshot(id = "qid-1",
                                question = "question 1",
                                questionType = Question.QuestionType.MULTI_CHOICE,
                                possibleAnswers = listOf(Question.PossibleAnswer("answer-1", "answer", true))))

                this.questions.add(
                        Interview.QuestionSnapshot(id = "qid-2",
                                question = "question 2",
                                questionType = Question.QuestionType.MULTI_CHOICE,
                                possibleAnswers = listOf(Question.PossibleAnswer("answer-1", "answer", true))))

                this.questions.add(
                        Interview.QuestionSnapshot(id = "qid-3",
                                question = "question 3",
                                questionType = Question.QuestionType.MULTI_CHOICE,
                                possibleAnswers = listOf(Question.PossibleAnswer("answer-1", "answer", true))))
            }.let {
                this.sections.add(it)
                interview = interviewService.saveInterview(this)
            }
        }.let {
            publishedInterview = interviewService.publishInterview(it.id.toString())
        }
    }

    @Test
    @WithMockUser
    fun saveInterviewSession() {

        InterviewSession(
                publishedInterview = publishedInterview,
                clientAccount = interview.clientAccount,
                userEmail = "joelin@geekhub.tw",
                name = "Joe Lin",
                interviewMode = InterviewSession.InterviewMode.REAL,
                duration = 1
        ).let {
            interviewSessionService.saveInterviewSession(it)

        }.let {
            interviewSessionService.startInterviewSession(it).also { session ->
                assertThat(session.interviewStartDate).isNotNull()
            }
        }.let {
            interviewSessionService.addAnswerAttempt(it, InterviewSession.QuestionAnswerAttempt(sectionId = interview.sections[0].id, questionSnapshotId = "qid-1", answerId = listOf("answer-1"))).run {
                assertThat(this.id).isNotNull()
                assertThat(this.answerAttemptSections).hasSize(1)

                this.answerAttemptSections.getValue(interview.sections[0].id).run {
                    assertThat(this.answerStats).hasSize(1)
                    assertThat(this.answerStats.getValue(Question.QuestionType.MULTI_CHOICE).questionTotal).isEqualTo(3)
                    assertThat(this.answerAttempts).hasSize(1)
                }
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
                assertThat(this.totalScore).isBetween(BigDecimal(0.33), BigDecimal(0.34))
                assertThat(this.answerAttemptSections).hasSize(1)

                this.answerAttemptSections.getValue(interview.sections[0].id).run {
                    assertThat(this.answerStats.getValue(Question.QuestionType.MULTI_CHOICE).answered).isEqualTo(1)
                    assertThat(this.answerStats.getValue(Question.QuestionType.MULTI_CHOICE).correct).isEqualTo(1)

                    assertThat(this.answerAttempts.getValue("qid-1").correct).isEqualTo(true)
                }
            }
        }
    }

    @Test
    @WithMockUser
    fun `verify failed add answer attempts`() {

        InterviewSession(
                publishedInterview = publishedInterview,
                clientAccount = interview.clientAccount,
                userEmail = "joelin@geekhub.tw",
                name = "Joe Lin",
                interviewMode = InterviewSession.InterviewMode.REAL,
                duration = 1
        ).let { it ->
            interviewSessionService.saveInterviewSession(it)
        }.let {
            assertThat {
                interviewSessionService.addAnswerAttempt(it, InterviewSession.QuestionAnswerAttempt(sectionId = "whatever", questionSnapshotId = "whatever", answerId = listOf("whatever")))
            }.isFailure().isInstanceOf(BusinessException::class)

            return@let it
        }.let {
            it.interviewStartDate = Date.from(Instant.now().minusSeconds(300))
            interviewSessionService.saveInterviewSession(it)
        }.let {
            assertThat {
                interviewSessionService.addAnswerAttempt(it, InterviewSession.QuestionAnswerAttempt(sectionId = "whatever", questionSnapshotId = "whatever", answerId = listOf("whatever")))
            }.isFailure().isInstanceOf(BusinessException::class)

            return@let it
        }.let {
            assertThat {
                interviewSessionService.submitInterviewSession(it)
            }.isFailure().isInstanceOf(BusinessException::class)
        }
    }
}