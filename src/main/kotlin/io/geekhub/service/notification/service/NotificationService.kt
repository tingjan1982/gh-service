package io.geekhub.service.notification.service

import io.geekhub.service.interview.model.InterviewSession

interface NotificationService {

    fun sendInterviewInvitation(interviewSession: InterviewSession)

    fun sendInterviewResult(interviewSession: InterviewSession)
}