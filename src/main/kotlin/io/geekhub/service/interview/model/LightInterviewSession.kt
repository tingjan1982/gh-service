package io.geekhub.service.interview.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class LightInterviewSession(
    @Id
    val id: String,
    var status: InterviewSession.Status
) {
    constructor(interviewSession: InterviewSession) : this(interviewSession.id.toString(), interviewSession.status)
}
