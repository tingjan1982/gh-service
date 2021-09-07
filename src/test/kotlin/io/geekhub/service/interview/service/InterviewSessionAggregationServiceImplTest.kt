package io.geekhub.service.interview.service

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.service.bean.SectionAverageStats
import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.shared.extensions.DummyObject
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import java.math.BigDecimal

@IntegrationTest
internal class InterviewSessionAggregationServiceImplTest {

    @Autowired
    lateinit var interviewSessionAggregationService: InterviewSessionAggregationService

    @Autowired
    lateinit var interviewSessionService: InterviewSessionService

    @Autowired
    lateinit var interviewService: InterviewService

    @Autowired
    lateinit var clientUser: ClientUser

    @Test
    @WithMockUser
    fun getAverageScores() {

        val section1: String
        val section2: String

        val interviewSession = DummyObject.dummyInterview(clientUser).let {
            Interview.Section(title = "default").apply {
                section1 = this.id

                this.questions.add(
                    Interview.QuestionSnapshot(
                        id = "qid-1",
                        question = "question 1",
                        questionType = Question.QuestionType.MULTI_CHOICE,
                        possibleAnswers = mutableListOf(Question.PossibleAnswer("answer-1", "answer", true))
                    )
                )

                this.questions.add(
                    Interview.QuestionSnapshot(
                        id = "qid-2",
                        question = "question 2",
                        questionType = Question.QuestionType.MULTI_CHOICE,
                        possibleAnswers = mutableListOf(Question.PossibleAnswer("answer-2", "answer", true))
                    )
                )

                this.questions.add(
                    Interview.QuestionSnapshot(
                        id = "qid-3",
                        question = "question 3",
                        questionType = Question.QuestionType.SHORT_ANSWER,
                        possibleAnswers = mutableListOf(Question.PossibleAnswer("answer-3", "answer", true))
                    )
                )

            }.let { section ->
                it.sections.add(section)
            }

            Interview.Section(title = "advanced").apply {
                section2 = this.id

                this.questions.add(
                    Interview.QuestionSnapshot(
                        id = "qid-a1",
                        question = "question a1",
                        questionType = Question.QuestionType.MULTI_CHOICE,
                        possibleAnswers = mutableListOf(Question.PossibleAnswer("answer-a1", "answer", true))
                    )
                )
            }.let { section ->
                it.sections.add(section)
            }

            interviewService.saveInterview(it)
        }.let {
            interviewSessionService.createInterviewSession(it).let { session ->
                interviewSessionService.startInterviewSession(session, clientUser)

            }.let { session ->
                it.sections[0].run {
                    val q = this.questions[0]
                    interviewSessionService.addAnswerAttempt(
                        session,
                        InterviewSession.QuestionAnswerAttempt(
                            sectionId = this.id,
                            questionSnapshotId = q.id,
                            answerIds = listOf(q.possibleAnswers[0].answerId)
                        )
                    )
                }

//                session.referencedInterview.sections[1].run {
//                    val q = this.questions[0]
//                    interviewSessionService.addAnswerAttempt(
//                        session, InterviewSession.QuestionAnswerAttempt(
//                            sectionId = this.id,
//                            questionSnapshotId = q.id,
//                            answerIds = listOf(q.possibleAnswers[0].answerId)
//                        )
//                    )
//                }

                interviewSessionService.submitInterviewSession(session)
            }
        }


        interviewSessionAggregationService.getAverageScores(interviewSession)?.run {
            println(this)

            assertThat(this.averageScore).hasSize(1)
            assertThat(this.averageScore[0].averageScore).isEqualTo(BigDecimal("0.25"))
            assertThat(this.averageScore[0].interviewSessionCount).isEqualTo(1)

            assertThat(this.sectionsAverageScore).hasSize(2)

            this.sectionsAverageScore.forEach { avg ->


            }

            assertThat(this.sectionsAverageScore.find { it.sectionId == section1 }!!).all {
                prop(SectionAverageStats.SectionAverageScore::sectionId).isNotNull()
                prop(SectionAverageStats.SectionAverageScore::questionTotal).isEqualTo(3)
                prop(SectionAverageStats.SectionAverageScore::correctTotal).isEqualTo(1)
                prop(SectionAverageStats.SectionAverageScore::averageSectionScore).isBetween(BigDecimal("0.3"), BigDecimal("0.4"))
            }

            assertThat(this.sectionsAverageScore.find { it.sectionId == section2 }!!).all {
                prop(SectionAverageStats.SectionAverageScore::sectionId).isNotNull()
                prop(SectionAverageStats.SectionAverageScore::questionTotal).isEqualTo(1)
                prop(SectionAverageStats.SectionAverageScore::correctTotal).isEqualTo(0)
                prop(SectionAverageStats.SectionAverageScore::averageSectionScore).isEqualTo(BigDecimal("0.00"))
            }
        }
    }
}