package io.geekhub.service.notification.service

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

    override fun sendInterviewInvitation(interviewSession: InterviewSession) {

        val emailRequest = SendGridRequest(
                SendGridRequest.EmailAddress("notification-noreply@geekhub.tw"),
                listOf(interviewSession.toInterviewInvitationPersonalization()),
                "d-60c9ba1a412a4281aa633136408f7185")

        sendNotification(emailRequest)
    }

    override fun sendInterviewResult(interviewSession: InterviewSession) {

        val emailRequest = SendGridRequest(
                SendGridRequest.EmailAddress("notification-noreply@geekhub.tw"),
                listOf(interviewSession.toInterviewResultPersonalization()),
                "d-9c6d72f8a66141de8e4b109c5aa9045f")

        sendNotification(emailRequest)
    }
    
    private fun sendNotification(emailRequest: SendGridRequest) {

        val headers = HttpHeaders().apply {
            this.setBearerAuth(apiKey)
            this.contentType = MediaType.APPLICATION_JSON
        }

        val requestEntity = HttpEntity<SendGridRequest>(emailRequest, headers)

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

fun InterviewSession.toInterviewInvitationPersonalization() = NotificationServiceImpl.SendGridRequest.Personalization(
        to = listOf(NotificationServiceImpl.SendGridRequest.EmailAddress(this.userEmail)),
        dynamic_template_data = mapOf(
                Pair("name", this.name ?: this.userEmail),
                Pair("companyName", this.clientUser.clientAccount.clientName),
                Pair("interviewSessionLink", "https://geekhub.tw/interviewSessions/${this.id}")
        )
)

fun InterviewSession.toInterviewResultPersonalization() = NotificationServiceImpl.SendGridRequest.Personalization(
        to = listOf(NotificationServiceImpl.SendGridRequest.EmailAddress(this.clientUser.email)),
        dynamic_template_data = mapOf(
                Pair("companyName", this.clientUser.clientAccount.clientName),
                Pair("candidateName", this.name ?: this.userEmail),
                Pair("interviewSessionLink", "https://geekhub.tw/interviewResult/${this.id}")
        )
)
