package io.geekhub.service.notification.service

import io.geekhub.service.interview.model.InterviewSession
import org.apache.commons.codec.Charsets
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class NotificationServiceImpl(val javaMailSender: JavaMailSender) : NotificationService {

    override fun sendNotification(interviewSession: InterviewSession) {
        val message = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, Charsets.UTF_8.name())

        helper.setFrom("joelin@geekhub.tw")
        helper.setTo(interviewSession.userEmail)
        helper.setSubject("GeekHub - Start Your Interview")
        helper.setText("Please click on the following link to begin your online interview experience.")

        javaMailSender.send(message)
    }
}