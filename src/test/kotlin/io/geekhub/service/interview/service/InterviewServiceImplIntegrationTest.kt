package io.geekhub.service.interview.service

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.specialization.repository.Specialization
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser

@IntegrationTest
internal class InterviewServiceImplIntegrationTest {

    @Autowired
    private lateinit var interviewService: InterviewService

    @Autowired
    private lateinit var clientAccount: ClientAccount

    @Autowired
    private lateinit var specialization: Specialization

    @Test
    @WithMockUser("dummy-user")
    fun createInterview() {

        Interview(title = "sample interview",
                jobTitle = "Engineer",
                clientAccount = clientAccount,
                specialization = specialization).let {
            this.interviewService.saveInterview(it)

        }.let {
            assertThat(it.id).isNotNull()
            assertThat(it.sections).isEmpty()

            val section = Interview.Section(title = "foundation").apply {
                questions.add(Interview.QuestionSnapshot("qid",
                        "dummy question",
                        Question.QuestionType.MULTI_CHOICE,
                        listOf(Question.PossibleAnswer(answer = "dummy answer", correctAnswer = true))))
            }

            it.sections.add(section)

            interviewService.saveInterview(it)
        }.let {
            assertThat(it.sections).all {
                hasSize(1)
                index(0).prop(Interview.Section::questions).hasSize(1)
            }

            assertThat(interviewService.getInterview(it.id.toString())).isNotNull()
        }
    }
}