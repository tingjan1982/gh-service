package io.geekhub.service.interview.web.model

import io.geekhub.service.account.web.model.ClientAccountResponse
import io.geekhub.service.interview.model.InterviewSession
import java.util.*

data class InterviewSessionResponse(
        val id: String,
        val interview: InterviewResponse,
        val clientAccount: ClientAccountResponse,
        val userEmail: String,
        val name: String?,
        val interviewMode: InterviewSession.InterviewMode,
        val duration: Int,
        val interviewSentDate: Date?,
        val interviewStartDate: Date?,
        val interviewEndDate: Date?,
        val score: Double,
        val answerAttempts: Map<String, InterviewSession.QuestionAnswerAttempt>,
        val followupInterviews: List<InterviewSession.FollowUpInterview>
) {

}