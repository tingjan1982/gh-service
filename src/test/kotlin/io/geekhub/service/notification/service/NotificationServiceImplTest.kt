package io.geekhub.service.notification.service

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.service.InterviewService
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.specialization.repository.Specialization
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser

@IntegrationTest
internal class NotificationServiceImplTest {

    @Autowired
    lateinit var notificationService: NotificationService

    @Autowired
    lateinit var interviewService: InterviewService

    @Autowired
    lateinit var clientAccount: ClientAccount

    @Autowired
    lateinit var specialization: Specialization

    @Test
    @WithMockUser
    fun sendInterviewInvitation() {
        val publishedInterview = Interview(title = "dummy interview", clientAccount = clientAccount, specialization = specialization, jobTitle = "Engineer").let {
            interviewService.saveInterview(it)
            interviewService.publishInterview(it.id.toString())
        }

        InterviewSession(publishedInterview = publishedInterview, clientAccount = clientAccount, userEmail = "joelin@geekhub.tw", interviewMode = InterviewSession.InterviewMode.REAL).let {
            notificationService.sendInterviewInvitation(it)
        }
    }

    @Test
    @WithMockUser
    fun sendInterviewResult() {
        val publishedInterview = Interview(title = "dummy interview", clientAccount = clientAccount, specialization = specialization, jobTitle = "Engineer").let {
            interviewService.saveInterview(it)
            interviewService.publishInterview(it.id.toString())
        }

        InterviewSession(publishedInterview = publishedInterview, clientAccount = clientAccount, userEmail = "candidate@geekhub.tw", interviewMode = InterviewSession.InterviewMode.REAL).let {
            notificationService.sendInterviewResult(it)
        }
    }
}