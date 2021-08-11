package io.geekhub.service.notification.service

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.interview.model.InterviewSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import javax.transaction.Transactional

@Service
@Transactional
class NotificationServiceImpl(@Value("\${spring.mail.password}") val apiKey: String) : NotificationService {

    val restTemplate = RestTemplate()

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(NotificationServiceImpl::class.java)
    }

    override fun sendInterviewInvitation(sender: ClientUser, interviewSession: InterviewSession) {

        val emailRequest = SendGridRequest(
                SendGridRequest.EmailAddress("notification-noreply@geekhub.tw"),
                listOf(interviewSession.toInterviewInvitationPersonalization(sender)),
                "d-60c9ba1a412a4281aa633136408f7185")

        sendNotification(emailRequest)
    }

    override fun sendInterviewResult(interviewSession: InterviewSession) {

        val emailRequest = SendGridRequest(
                SendGridRequest.EmailAddress("notification-noreply@geekhub.tw"),
                listOf(interviewSession.toInterviewResultPersonalization()),
                "d-bbd50935af584855b0c45f4057f185df")

        sendNotification(emailRequest)
    }

    override fun sendOrganizationInvitation(invitation: ClientAccount.UserInvitation, clientAccount: ClientAccount) {

        val emailRequest = SendGridRequest(
            SendGridRequest.EmailAddress("notification-noreply@geekhub.tw"),
            listOf(clientAccount.toOrganizationInvitationPersonalization(invitation)),
            "d-dd747e161f77482c8bf94ba3479dfd95")

        sendNotification(emailRequest)
    }
    
    private fun sendNotification(emailRequest: SendGridRequest) {

        val headers = HttpHeaders().apply {
            this.setBearerAuth(apiKey)
            this.contentType = MediaType.APPLICATION_JSON
        }

        val requestEntity = HttpEntity(emailRequest, headers)

        restTemplate.exchange("https://api.sendgrid.com/v3/mail/send", HttpMethod.POST, requestEntity, String::class.java).run {
            LOGGER.debug(this.body)
        }
        
    }

    data class SendGridRequest(
            val from: EmailAddress,
            val personalizations: List<Personalization>,
            val template_id: String?,
            val subject: String? = null,
            val content: List<EmailContent>? = null
    ) {

        data class Personalization(val to: List<EmailAddress>, val dynamic_template_data: Map<String, String>)

        data class EmailAddress(val email: String)

        data class EmailContent(val type: String, val value: String)
    }
}

fun InterviewSession.toInterviewInvitationPersonalization(sender: ClientUser) = NotificationServiceImpl.SendGridRequest.Personalization(
        to = listOf(NotificationServiceImpl.SendGridRequest.EmailAddress(this.userEmail)),
        dynamic_template_data = mapOf(
                Pair("candidateName", this.name ?: this.userEmail),
                Pair("senderName", sender.name),
                Pair("clientAccountName", this.clientUser.clientAccount.clientName),
                Pair("interviewSessionLink", "https://geekhub.tw/interviews/${this.id}/test")
        )
)

fun InterviewSession.toInterviewResultPersonalization() = NotificationServiceImpl.SendGridRequest.Personalization(
        to = listOf(NotificationServiceImpl.SendGridRequest.EmailAddress(this.clientUser.email)),
        dynamic_template_data = mapOf(
                Pair("candidateName", this.name ?: this.userEmail),
                Pair("clientAccountName", this.clientUser.clientAccount.clientName),             
                Pair("interviewSessionLink", "https://geekhub.tw/manageInterviews/${this.currentInterview.id}/${this.id}")
        )
)

fun ClientAccount.toOrganizationInvitationPersonalization(invitation: ClientAccount.UserInvitation) = NotificationServiceImpl.SendGridRequest.Personalization(
    to = listOf(NotificationServiceImpl.SendGridRequest.EmailAddress(invitation.email)),
    dynamic_template_data = mapOf(
        Pair("organization", invitation.inviterOrganization),
        Pair("inviterName", invitation.inviterName),
        Pair("invitationLink", "https://geekhub.tw/organization")
    )
)