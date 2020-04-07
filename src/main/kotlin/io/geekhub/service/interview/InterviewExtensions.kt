package io.geekhub.service.interview

import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.web.model.AnswerAttemptRequest
import io.geekhub.service.interview.web.model.InterviewSessionRequest
import io.geekhub.service.interview.web.model.InterviewSessionResponse
import io.geekhub.service.shared.extensions.toDTO

fun InterviewSessionRequest.toEntity(interview: Interview) = InterviewSession(
        interview = interview,
        clientAccount = interview.clientAccount,
        userEmail = this.userEmail,
        name = this.name,
        interviewMode = this.interviewMode,
        duration = this.duration
)

fun InterviewSession.toDTO() = InterviewSessionResponse(
        this.id.toString(),
        this.interview.toDTO(),
        this.clientAccount.toDTO(),
        this.userEmail,
        this.name,
        this.interviewMode,
        this.duration,
        this.interviewSentDate,
        this.interviewStartDate,
        this.interviewEndDate,
        this.score,
        this.answerAttempts,
        this.followupInterviews
)

fun AnswerAttemptRequest.toEntity() = InterviewSession.QuestionAnswerAttempt(
        answerId = answerId,
        answer = answer
)