package io.geekhub.service.interview

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.model.PublishedInterview
import io.geekhub.service.interview.service.bean.SectionAverageStats
import io.geekhub.service.interview.web.model.*
import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.shared.model.Visibility
import java.math.BigDecimal

fun PublishedInterview.toDTO(currentUser: ClientUser) = PublishedInterviewResponse(
        this.id,
        this.referencedInterview.toDTO(currentUser, true)
)

fun InterviewSessionRequest.toEntity(interview: PublishedInterview, clientUser: ClientUser): InterviewSession {

    val duration = if (this.duration > 0) {
        this.duration
    } else {
        interview.referencedInterview.defaultDuration
    }

    return InterviewSession(
            publishedInterview = interview,
            currentInterview = interview.referencedInterview,
            clientUser = clientUser,
            userEmail = this.userEmail,
            name = this.name,
            interviewMode = this.interviewMode,
            duration = duration
    )
}

fun InterviewSession.showCorrectAnswer(currentUser: ClientUser): Boolean {

    val (isOwner, isPublicInterview, releaseResult) = this.publishedInterview.referencedInterview.let {
        val interviewOwner = it.clientUser.id == currentUser.id
        val publicInterview = it.visibility == Visibility.PUBLIC
        val releaseResult = it.releaseResult == Interview.ReleaseResult.YES

        return@let Triple(interviewOwner, publicInterview, releaseResult)
    }

    return isOwner || releaseResult || isPublicInterview && this.status == InterviewSession.Status.ENDED
}

fun InterviewSession.toDTO(currentUser: ClientUser): InterviewSessionResponse {

    val updatedAnswerAttemptSection = if (this.showCorrectAnswer(currentUser)) {
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
            this.publishedInterview.referencedInterview.toDTO(currentUser, true),
            this.clientUser.toDTO(),
            this.userEmail,
            this.name,
            this.candidateUser?.toDTO(),
            this.interviewMode,
            this.duration,
            this.status,
            this.interviewSentDate,
            this.interviewStartDate,
            this.interviewEndDate,
            this.totalScore,
            updatedAnswerAttemptSection,
            this.currentInterview.groupInterviewSessions(),
            this.followupInterviews
    )
}

fun InterviewSession.toLightDTO() = InterviewSessionsResponse.LightInterviewSessionResponse(
        this.id.toString(),
        this.publishedInterview.referencedInterview.toLightDTO(),
        this.clientUser.toDTO(),
        this.userEmail,
        this.name,
        this.candidateUser?.toDTO(),
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

fun SectionAverageStats.toDTO() = InterviewSessionAverageStatsResponse(
        averageScore = if (this.averageScore.isNotEmpty()) {
            this.averageScore[0]
        } else {
            SectionAverageStats.OverallAverageScore("", BigDecimal.ZERO, 0)
        },
        sectionsAverageScore = this.sectionsAverageScore
)