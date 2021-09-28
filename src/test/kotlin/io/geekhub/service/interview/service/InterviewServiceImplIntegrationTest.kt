package io.geekhub.service.interview.service

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.account.service.ClientUserService
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.exception.OwnershipException
import io.geekhub.service.shared.extensions.DummyObject
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
    private lateinit var clientUserService: ClientUserService

    @Autowired
    private lateinit var clientUser: ClientUser

    @Test
    @WithMockUser("dummy-user")
    fun createInterview() {

        val interview = Interview(
            title = "sample interview",
            description = "<img src=\\\"nonexistent.png\\\" onerror=\\\"alert(localStorage);\\\" />",
            jobTitle = "Engineer",
            clientUser = clientUser,
            visibility = Visibility.PUBLIC,
            releaseResult = Interview.ReleaseResult.YES
        ).let {
            Interview.Section(title = "foundation").apply {
                questions.add(
                    Interview.QuestionSnapshot(
                        "qid",
                        "<script>dummy question</script>",
                        Question.QuestionType.MULTI_CHOICE,
                        mutableListOf(Question.PossibleAnswer(answer = "<script>dummy answer</script>", correctAnswer = true))
                    )
                )
            }.let { sec ->
                it.sections.add(sec)
            }

            assertThat {
                interviewService.saveInterview(it)
            }.isFailure().isInstanceOf(BusinessException::class)

            it.sections[0].questions[0].possibleAnswers.add(Question.PossibleAnswer(answer = "incorrect", correctAnswer = false))

            interviewService.saveInterview(it)

        }.let {
            assertThat(it.id).isNotNull()
            assertThat(it.latestPublishedInterviewId).isNotNull()
            assertThat(it.description).isNullOrEmpty()
            assertThat(it.sections).all {
                hasSize(1)
                index(0).prop(Interview.Section::questions).hasSize(1)
                index(0).prop(Interview.Section::questions).index(0).all {
                    prop(Interview.QuestionSnapshot::question).isEmpty()
                    prop(Interview.QuestionSnapshot::possibleAnswers).index(0).prop(Question.PossibleAnswer::answer).isEmpty()
                }
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

        interviewService.copyInterview(interview, clientUser).let { copy ->
            assertThat(copy.id).isNotEqualTo(interview.id)
            assertThat(copy.title).contains(interview.title)
            assertThat(copy.latestPublishedInterviewId).isNotNull()
            assertThat(copy.sections).isSameAs(interview.sections)
            assertThat(copy.likeCount).isZero()
            assertThat(copy.userKey).isNull()
        }

        val anotherUser = DummyObject.dummyClientUser(clientUser.clientAccount).apply {
            this.name = "Another User"
        }.let {
            clientUserService.saveClientUser(it)
        }

        assertThat {
            interviewService.copyInterview(interview, anotherUser)
        }.isFailure().isInstanceOf(OwnershipException::class)

    }
}