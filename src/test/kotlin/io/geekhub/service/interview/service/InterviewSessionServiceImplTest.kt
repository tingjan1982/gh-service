package io.geekhub.service.interview.service

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.specialization.repository.Specialization
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser

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


    @Test
    @WithMockUser("mockUser")
    fun saveInterviewSession() {

        Interview(title = "dummy interview", jobTitle = "Engineer", clientAccount = clientAccount, specialization = specialization).let {
            interviewService.saveInterview(it)

            InterviewSession(
                    interview = it,
                    clientAccount = it.clientAccount,
                    userEmail = "joelin@geekhub.tw",
                    interviewMode = InterviewSession.InterviewMode.REAL
            ).let { session ->
                interviewSessionService.saveInterviewSession(session).run {
                    assertThat(this.id).isNotNull()
                }

                interviewSessionService.getInterviewSession(session.id.toString()).run {
                    assertThat(this).all {
                        isNotNull()
                        isEqualTo(session)
                    }
                }
            }
        }
    }
}