package io.geekhub.service.notification.service

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.interview.service.InterviewService
import io.geekhub.service.interview.service.InterviewSessionService
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.shared.extensions.DummyObject
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
    lateinit var interviewSessionService: InterviewSessionService

    @Autowired
    lateinit var clientAccount: ClientAccount

    @Autowired
    lateinit var clientUser: ClientUser

    @Test
    @WithMockUser
    fun sendInterviewInvitation() {

        DummyObject.dummyInterview(clientUser = clientUser).let {
            interviewService.saveInterview(it)
        }.let {
            interviewSessionService.createInterviewSession(it)
        }.let {
            notificationService.sendInterviewInvitation(clientUser, it)
        }
    }

    @Test
    @WithMockUser
    fun sendInterviewResult() {

        DummyObject.dummyInterview(clientUser = clientUser).let {
            interviewService.saveInterview(it)
        }.let {
            interviewSessionService.createInterviewSession(it)
        }.let {
            notificationService.sendInterviewResult(it)
        }
    }

    @Test
    @WithMockUser
    fun sendOrganizationInvitation() {

        val invitation = ClientAccount.UserInvitation(
            clientUser.id.toString(),
            clientUser.name,
            clientUser.email,
            clientAccount.clientName,
            clientAccount.id,
            "joelin@geekhub.tw"
        )

        notificationService.sendOrganizationInvitation(invitation, clientUser)
    }
}