package io.geekhub.service.interview.web.model

import io.geekhub.service.account.web.model.ClientUserResponse
import io.geekhub.service.interview.model.InterviewSession
import java.math.BigDecimal
import java.util.*

data class InterviewSessionResponse(
        val id: String,
        val interview: InterviewResponse,
        val clientUser: ClientUserResponse,
        val userEmail: String,
        val name: String?,
        val candidateUser: ClientUserResponse?,
        val interviewMode: InterviewSession.InterviewMode,
        val duration: Int,
        val status: InterviewSession.Status,
        val interviewSentDate: Date?,
        val interviewStartDate: Date?,
        val interviewEndDate: Date?,
        val totalScore: BigDecimal,
        val answerAttemptSections: Map<String, InterviewSession.AnswerAttemptSection>,
        val followupInterviews: List<InterviewSession.FollowUpInterview>
)
