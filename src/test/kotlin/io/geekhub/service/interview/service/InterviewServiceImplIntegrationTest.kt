package io.geekhub.service.interview.service

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.model.SearchCriteria
import io.geekhub.service.shared.model.Visibility
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser

@IntegrationTest
internal class InterviewServiceImplIntegrationTest {

    @Autowired
    private lateinit var interviewService: InterviewService

    @Autowired
    private lateinit var interviewSessionService: InterviewSessionService

    @Autowired
    private lateinit var clientUser: ClientUser

    @Test
    @WithMockUser("dummy-user")
    fun createInterview() {

        val interview = Interview(
            title = "sample interview",
            jobTitle = "Engineer",
            clientUser = clientUser,
            visibility = Visibility.PUBLIC,
            releaseResult = Interview.ReleaseResult.YES
        ).let {
            Interview.Section(title = "foundation").apply {
                questions.add(
                    Interview.QuestionSnapshot(
                        "qid",
                        "dummy question",
                        Question.QuestionType.MULTI_CHOICE,
                        listOf(Question.PossibleAnswer(answer = "dummy answer", correctAnswer = true))
                    )
                )
            }.let { sec ->
                it.sections.add(sec)
            }

            this.interviewService.saveInterview(it)

        }.let {
            assertThat(it.id).isNotNull()
            assertThat(it.latestPublishedInterviewId).isNotNull()
            assertThat(it.sections).all {
                hasSize(1)
                index(0).prop(Interview.Section::questions).hasSize(1)
            }

            it
        }.let {
            assertThat(interviewService.getInterview(it.id.toString())).isNotNull()

            it
        }.let {
            interviewService.getPublishedInterviewByInterview(it.id.toString()).run {
                assertThat(this.referencedInterview.id).isEqualTo(it.id)
                assertThat(this.referencedInterview.latestPublishedInterviewId).isEqualTo(this.id)
            }

            return@let it
        }

        interviewService.getInterviews(SearchCriteria.fromRequestParameters(clientUser, mapOf())).let {
            assertThat(it.totalElements).isNotZero()
        }


        interviewService.getInterviews(SearchCriteria.fromRequestParameters(clientUser, mapOf("template" to "true"))).let {
            assertThat(it.totalElements).isZero()
        }

        interviewSessionService.createInterviewSession(interview)

        interview.apply {
            title = "this update should not work because interview session exists"
        }.let {
            assertThat {
                interviewService.saveInterview(it)
            }.isFailure().isInstanceOf(BusinessException::class)
        }
    }
}