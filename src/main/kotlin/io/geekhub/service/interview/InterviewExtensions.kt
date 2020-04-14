package io.geekhub.service.interview

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.model.PublishedInterview
import io.geekhub.service.interview.web.model.*
import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.shared.extensions.toLightDTO

fun PublishedInterview.toDTO() = PublishedInterviewResponse(
        this.id.toString(),
        this.referencedInterview.toDTO()
)

fun InterviewSessionRequest.toEntity(interview: PublishedInterview, clientAccount: ClientAccount) = InterviewSession(
        publishedInterview = interview,
        clientAccount = clientAccount,
        userEmail = this.userEmail,
        name = this.name,
        interviewMode = this.interviewMode,
        duration = this.duration
)

fun InterviewSession.toDTO() = InterviewSessionResponse(
        this.id.toString(),
        this.publishedInterview.referencedInterview.toDTO(false),
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

fun InterviewSession.toLightDTO() = InterviewSessionsResponse.LightInterviewSessionResponse(
        this.id.toString(),
        this.publishedInterview.referencedInterview.toLightDTO(),
        this.clientAccount.toDTO(),
        this.userEmail,
        this.name,
        this.interviewMode,
        this.duration,
        this.interviewSentDate,
        this.interviewStartDate,
        this.interviewEndDate,
        this.score
)

fun AnswerAttemptRequest.toEntity() = InterviewSession.QuestionAnswerAttempt(
        answerId = answerId,
        answer = answer
)