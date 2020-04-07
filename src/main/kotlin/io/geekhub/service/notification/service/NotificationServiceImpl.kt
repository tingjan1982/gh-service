package io.geekhub.service.notification.service

import io.geekhub.service.interview.model.InterviewSession
import org.apache.commons.codec.Charsets
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import javax.transaction.Transactional

@Service
@Transactional
class NotificationServiceImpl(val javaMailSender: JavaMailSender, @Value("\${spring.mail.password}") val apiKey: String) : NotificationService {

    val restTemplate = RestTemplate()

    override fun sendNotification(interviewSession: InterviewSession) {

        val emailRequest = SendGridRequest(
                SendGridRequest.EmailAddress("joelin@geekhub.tw"),
                listOf(interviewSession.toPersonalization()),
                "d-9c6d72f8a66141de8e4b109c5aa9045f")

        val headers = HttpHeaders().apply {
            this.setBearerAuth(apiKey)
            this.contentType = MediaType.APPLICATION_JSON
        }

        val requestEntity = HttpEntity<SendGridRequest>(emailRequest, headers)
        restTemplate.exchange("https://api.sendgrid.com/v3/mail/send", HttpMethod.POST, requestEntity, String::class.java)
    }

    private fun sendEmailUsingSMTP(interviewSession: InterviewSession) {

        val message = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, Charsets.UTF_8.name())

        helper.setFrom("joelin@geekhub.tw")
        helper.setTo(interviewSession.userEmail)
        helper.setSubject("GeekHub - Start Your Interview")
        helper.setText("Please click on the following link to begin your online interview experience.")

        javaMailSender.send(message)
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

fun InterviewSession.toPersonalization() = NotificationServiceImpl.SendGridRequest.Personalization(
        to = listOf(NotificationServiceImpl.SendGridRequest.EmailAddress(this.userEmail)),
        dynamic_template_data = mapOf(
                Pair("name", this.name ?: this.userEmail),
                Pair("companyName", this.clientAccount.clientName),
                Pair("interviewSessionLink", "https://geekhub.tw/interviewSessions/${this.id}")
        )
)
