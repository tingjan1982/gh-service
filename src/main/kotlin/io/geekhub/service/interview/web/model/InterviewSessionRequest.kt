package io.geekhub.service.interview.web.model

import io.geekhub.service.interview.model.InterviewSession

data class InterviewSessionRequest(
        val interviewId: String,
        val userEmail: String,
        val name: String?,
        val interviewMode: InterviewSession.InterviewMode = InterviewSession.InterviewMode.REAL,
        val duration: Int = -1
)
