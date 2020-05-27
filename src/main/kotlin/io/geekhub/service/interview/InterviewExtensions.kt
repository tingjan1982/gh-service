package io.geekhub.service.interview

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.model.PublishedInterview
import io.geekhub.service.interview.web.model.*
import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.shared.extensions.toLightDTO
import io.geekhub.service.shared.model.Visibility

fun PublishedInterview.toDTO(currentAccount: ClientAccount) = PublishedInterviewResponse(
        this.id,
        this.referencedInterview.toDTO(currentAccount)
)

fun InterviewSessionRequest.toEntity(interview: PublishedInterview, clientAccount: ClientAccount): InterviewSession {

    val duration = if (this.duration > 0) {
        this.duration
    } else {
        interview.referencedInterview.defaultDuration
    }

    return InterviewSession(
            publishedInterview = interview,
            clientAccount = clientAccount,
            userEmail = this.userEmail,
            name = this.name,
            interviewMode = this.interviewMode,
            duration = duration
    )
}

fun InterviewSession.showCorrectAnswer(currentAccount: ClientAccount): Boolean {

    val (isPublicInterview, isOwner) = this.publishedInterview.referencedInterview.let {
        val publicInterview = it.visibility == Visibility.PUBLIC
        val interviewOwner = it.clientAccount.id == currentAccount.id

        return@let Pair(publicInterview, interviewOwner)
    }

    return isOwner || isPublicInterview && this.status == InterviewSession.Status.ENDED
}

fun InterviewSession.toDTO(currentAccount: ClientAccount): InterviewSessionResponse {

    val updatedAnswerAttemptSection = if (this.showCorrectAnswer(currentAccount)) {
        this.answerAttemptSections
    } else {
        this.answerAttemptSections.forEach { section ->
            section.value.answerStats.forEach { stats ->
                stats.value.correct = 0
            }

            section.value.answerAttempts.forEach { attempt ->
                attempt.value.correct = null
            }
        }

        this.answerAttemptSections
    }

    return InterviewSessionResponse(
            this.id.toString(),
            this.publishedInterview.referencedInterview.toDTO(currentAccount),
            this.clientAccount.toDTO(),
            this.userEmail,
            this.name,
            this.candidateAccount?.toDTO(),
            this.interviewMode,
            this.duration,
            this.status,
            this.interviewSentDate,
            this.interviewStartDate,
            this.interviewEndDate,
            this.totalScore,
            updatedAnswerAttemptSection,
            this.followupInterviews
    )
}

fun InterviewSession.toLightDTO() = InterviewSessionsResponse.LightInterviewSessionResponse(
        this.id.toString(),
        this.publishedInterview.referencedInterview.toLightDTO(),
        this.clientAccount.toDTO(),
        this.userEmail,
        this.name,
        this.candidateAccount?.toDTO(),
        this.interviewMode,
        this.duration,
        this.status,
        this.interviewSentDate,
        this.interviewStartDate,
        this.interviewEndDate,
        this.totalScore
)

fun AnswerAttemptRequest.toEntity() = InterviewSession.QuestionAnswerAttempt(
        sectionId = this.sectionId,
        questionSnapshotId = this.questionSnapshotId,
        answerIds = this.answerId,
        answer = this.answer
)