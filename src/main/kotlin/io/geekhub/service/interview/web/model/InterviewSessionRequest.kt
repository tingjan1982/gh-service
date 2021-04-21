package io.geekhub.service.interview.web.model

import io.geekhub.service.interview.model.InterviewSession
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class InterviewSessionRequest(
        @field:NotBlank val interviewId: String,
        @field:NotBlank @field:Email val userEmail: String,
        val name: String?,
        val interviewMode: InterviewSession.InterviewMode = InterviewSession.InterviewMode.REAL,
        val duration: Int = -1
)
