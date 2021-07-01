package io.geekhub.service.interview.web.model

import io.geekhub.service.account.web.model.LightClientUserResponse
import io.geekhub.service.interview.model.InterviewSession
import java.math.BigDecimal
import java.util.*

data class InterviewSessionResponse(
        val id: String,
        val interview: InterviewResponse,
        val clientUser: LightClientUserResponse,
        val userEmail: String,
        val name: String?,
        val candidateUser: LightClientUserResponse?,
        val interviewMode: InterviewSession.InterviewMode,
        val duration: Int,
        val status: InterviewSession.Status,
        val interviewSentDate: Date?,
        val interviewStartDate: Date?,
        val interviewEndDate: Date?,
        val totalScore: BigDecimal,
        val answerAttemptSections: Map<String, InterviewSession.AnswerAttemptSection>,
        val groupedInterviewSessions: Map<InterviewSession.Status, List<String>> = mapOf(),
        val followupInterviews: List<InterviewSession.FollowUpInterview>
)
