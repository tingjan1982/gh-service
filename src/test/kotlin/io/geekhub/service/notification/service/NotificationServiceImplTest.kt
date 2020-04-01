package io.geekhub.service.notification.service

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.specialization.repository.Specialization
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
internal class NotificationServiceImplTest {

    @Autowired
    lateinit var notificationService: NotificationService

    @Autowired
    lateinit var clientAccount: ClientAccount

    @Autowired
    lateinit var specialization: Specialization


    @Test
    fun sendNotification() {
        val interview = Interview(title = "dummy interview", clientAccount = clientAccount, specialization = specialization, jobTitle = "Engineer")

        InterviewSession(interview = interview, clientAccount = clientAccount, userEmail = "joelin@geekhub.tw", interviewMode = InterviewSession.InterviewMode.REAL).let {
            notificationService.sendNotification(it)
        }
    }
}