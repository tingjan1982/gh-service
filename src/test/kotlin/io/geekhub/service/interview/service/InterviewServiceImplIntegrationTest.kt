package io.geekhub.service.interview.service

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.repository.InterviewRepository
import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.model.Visibility
import io.geekhub.service.specialization.repository.Specialization
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser

@IntegrationTest
internal class InterviewServiceImplIntegrationTest {

    @Autowired
    private lateinit var interviewService: InterviewService

    @Autowired
    private lateinit var interviewRepository: InterviewRepository

    @Autowired
    private lateinit var clientUser: ClientUser

    @Autowired
    private lateinit var specialization: Specialization

    @Test
    @WithMockUser("dummy-user")
    fun createInterview() {

        Interview(
            title = "sample interview",
            jobTitle = "Engineer",
            clientUser = clientUser,
            specialization = specialization,
            visibility = Visibility.PUBLIC,
            releaseResult = Interview.ReleaseResult.YES
        ).let {
            this.interviewService.saveInterview(it)

        }.let {
            assertThat(it.id).isNotNull()
            assertThat(it.sections).isEmpty()

            assertThat {
                interviewService.publishInterview(it.id.toString())
            }.isFailure().isInstanceOf(BusinessException::class)

            it.sections.add(Interview.Section(title = "foundation"))

            interviewService.saveInterview(it)

            assertThat {
                interviewService.publishInterview(it.id.toString())
            }.isFailure().isInstanceOf(BusinessException::class)

            it
        }.let {
            val section = Interview.Section(title = "foundation").apply {
                questions.add(
                    Interview.QuestionSnapshot(
                        "qid",
                        "dummy question",
                        Question.QuestionType.MULTI_CHOICE,
                        listOf(Question.PossibleAnswer(answer = "dummy answer", correctAnswer = true))
                    )
                )
            }

            it.sections = mutableListOf(section)

            interviewService.saveInterview(it)
        }.let {
            assertThat(it.sections).all {
                hasSize(1)
                index(0).prop(Interview.Section::questions).hasSize(1)
            }

            assertThat(interviewService.getInterview(it.id.toString())).isNotNull()

            it
        }.let {
            interviewService.publishInterview(it.id.toString()).run {
                assertThat(this.referencedInterview.id).isEqualTo(it.id)
                assertThat(this.referencedInterview.latestPublishedInterviewId).isEqualTo(this.id)
            }
        }

        assertThat(interviewRepository.countBySpecialization(specialization)).isEqualTo(1)
    }
}