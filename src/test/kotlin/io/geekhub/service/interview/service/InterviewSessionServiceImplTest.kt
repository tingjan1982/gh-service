package io.geekhub.service.interview.service

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.model.PublishedInterview
import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.model.Visibility
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
    lateinit var clientUser: ClientUser

    lateinit var interview: Interview

    lateinit var publishedInterview: PublishedInterview

    @BeforeEach
    fun prepareInterview() {
        Interview(
            title = "dummy interview",
            jobTitle = "Engineer",
            clientUser = clientUser,
            visibility = Visibility.PUBLIC,
            releaseResult = Interview.ReleaseResult.YES
        ).apply {
            Interview.Section(title = "default").apply {
                this.questions.add(
                    Interview.QuestionSnapshot(
                        id = "qid-1",
                        question = "question 1",
                        questionType = Question.QuestionType.MULTI_CHOICE,
                        possibleAnswers = listOf(Question.PossibleAnswer("answer-1", "answer", true))
                    )
                )

                this.questions.add(
                    Interview.QuestionSnapshot(
                        id = "qid-2",
                        question = "question that will be answered",
                        questionType = Question.QuestionType.SHORT_ANSWER
                    )
                )

                this.questions.add(
                    Interview.QuestionSnapshot(
                        id = "qid-3",
                        question = "question left unanswered",
                        questionType = Question.QuestionType.SHORT_ANSWER
                    )
                )
            }.let {
                this.sections.add(it)
            }

        }.let {
            interview = interviewService.saveInterview(it)
        }
    }

    @Test
    @WithMockUser
    fun saveInterviewSession() {

        val sectionId = interview.sections[0].id

        interviewSessionService.createInterviewSession(interview).let {
            assertThat(it.status).isEqualTo(InterviewSession.Status.NOT_STARTED)

            interviewSessionService.startInterviewSession(it, clientUser).run {
                assertThat(this.interviewStartDate).isNotNull()
                assertThat(this.status).isEqualTo(InterviewSession.Status.STARTED)
                assertThat(this.answerAttemptSections).hasSize(1)

                this.answerAttemptSections.getValue(sectionId).run {
                    assertThat(this.answerStats).hasSize(2)
                    assertThat(this.answerStats.getValue(Question.QuestionType.MULTI_CHOICE).questionTotal).isEqualTo(1)
                    assertThat(this.answerStats.getValue(Question.QuestionType.SHORT_ANSWER).questionTotal).isEqualTo(2)
                }
            }

            assertThat(interviewSessionService.getCurrentInterviewSession(interview.id.toString(), clientUser))
                .isNotNull()
                .isEqualTo(it)

            it
        }.let {
            interviewSessionService.addAnswerAttempt(
                it,
                InterviewSession.QuestionAnswerAttempt(sectionId = sectionId, questionSnapshotId = "qid-1", answerIds = listOf("answer-1"))
            ).run {
                this.answerAttemptSections.getValue(sectionId).run {
                    assertThat(this.answerAttempts).hasSize(1)
                }
            }

            interviewSessionService.addAnswerAttempt(
                it,
                InterviewSession.QuestionAnswerAttempt(sectionId = sectionId, questionSnapshotId = "qid-2", answer = "short answer")
            ).run {
                this.answerAttemptSections.getValue(sectionId).run {
                    assertThat(this.answerAttempts).hasSize(2)
                }
            }

            interviewSessionService.getInterviewSession(it.id.toString()).run {
                assertThat(this).all {
                    isNotNull()
                    isEqualTo(it)
                }
            }

            it
        }.let {
            assertThat {
                interviewSessionService.startInterviewSession(it, clientUser)
            }.isFailure().isInstanceOf(BusinessException::class)

            it
        }.let {
            interviewSessionService.submitInterviewSession(it).run {
                assertThat(this.interviewEndDate).isNotNull()
                assertThat(this.status).isEqualTo(InterviewSession.Status.ENDED)
                assertThat(this.answerAttemptSections).hasSize(1)

                this.answerAttemptSections.getValue(sectionId).run {
                    assertThat(this.answerStats.getValue(Question.QuestionType.MULTI_CHOICE).answered).isEqualTo(1)
                    assertThat(this.answerStats.getValue(Question.QuestionType.MULTI_CHOICE).correct).isEqualTo(1)

                    assertThat(this.answerAttempts.getValue("qid-1").correct).isEqualTo(true)
                }
            }

            interviewSessionService.markInterviewSessionAnswer(it, sectionId, "qid-2", false)
            interviewSessionService.markInterviewSessionAnswer(it, sectionId, "qid-2", true).run {
                this.answerAttemptSections.getValue(sectionId).run {
                    assertThat(this.answerStats.getValue(Question.QuestionType.SHORT_ANSWER).answered).isEqualTo(1)
                    assertThat(this.answerStats.getValue(Question.QuestionType.SHORT_ANSWER).correct).isEqualTo(1)

                    assertThat(this.answerAttempts.getValue("qid-2").correct).isEqualTo(true)
                }
            }

            interviewSessionService.markInterviewSessionAnswer(it, sectionId, "qid-3", false).run {
                this.answerAttemptSections.getValue(sectionId).run {
                    assertThat(this.answerStats.getValue(Question.QuestionType.SHORT_ANSWER).answered).isEqualTo(2)
                    assertThat(this.answerStats.getValue(Question.QuestionType.SHORT_ANSWER).correct).isEqualTo(1)

                    assertThat(this.answerAttempts.getValue("qid-3").correct).isEqualTo(false)
                }
            }

            it
        }.let {
            interviewSessionService.calculateScore(it).run {
                assertThat(this.totalScore).isBetween(BigDecimal(0.6), BigDecimal(0.7))
            }
        }
    }

    @Test
    @WithMockUser
    fun `verify exception cases in interview session lifecycle`() {

        interviewSessionService.createInterviewSession(interview).let {
            assertThat {
                interviewSessionService.addAnswerAttempt(
                    it,
                    InterviewSession.QuestionAnswerAttempt(sectionId = "whatever", questionSnapshotId = "whatever", answerIds = listOf("whatever"))
                )
            }.isFailure().isInstanceOf(BusinessException::class)

            it
        }.let {
            interviewSessionService.startInterviewSession(it, clientUser)

            it.interviewStartDate = Date.from(Instant.now().minusSeconds(60 * 120))
            interviewSessionService.saveInterviewSession(it)
        }.let {
            assertThat {
                interviewSessionService.addAnswerAttempt(
                    it,
                    InterviewSession.QuestionAnswerAttempt(sectionId = "whatever", questionSnapshotId = "whatever", answerIds = listOf("whatever"))
                )
            }.isFailure().isInstanceOf(BusinessException::class)

            it
        }.let {
            assertThat {
                interviewSessionService.markInterviewSessionAnswer(it, sectionId = "whatever", questionSnapshotId = "whatever", correct = true)

            }.isFailure().isInstanceOf(BusinessException::class)

            interviewSessionService.submitInterviewSession(it)

            assertThat {
                interviewSessionService.addAnswerAttempt(
                    it,
                    InterviewSession.QuestionAnswerAttempt(sectionId = "whatever", questionSnapshotId = "whatever", answerIds = listOf("whatever"))
                )
            }.isFailure().isInstanceOf(BusinessException::class)
        }
    }

    @Test
    @WithMockUser
    fun `check interview's interviewSessions reference`() {

        interviewSessionService.createInterviewSession(interview).let {
            interviewService.getInterview(interview.id.toString()).run {
                assertThat(this.lightInterviewSessions).hasSize(1)

                assertThat(this.groupInterviewSessions()).all {
                    hasSize(1)
                    this.key(InterviewSession.Status.NOT_STARTED).hasSize(1)
                }
            }

            it
        }.let {
            interviewSessionService.startInterviewSession(it, clientUser).also {
                interviewService.getInterview(interview.id.toString()).run {
                    assertThat(this.lightInterviewSessions).hasSize(1)

                    assertThat(this.groupInterviewSessions()).all {
                        hasSize(1)
                        this.key(InterviewSession.Status.STARTED).hasSize(1)
                    }
                }
            }

            it
        }
    }
}