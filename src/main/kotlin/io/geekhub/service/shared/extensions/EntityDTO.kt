package io.geekhub.service.shared.extensions

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.account.web.model.ClientAccountResponse
import io.geekhub.service.account.web.model.ClientUserResponse
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.web.model.InterviewRequest
import io.geekhub.service.interview.web.model.InterviewResponse
import io.geekhub.service.interview.web.model.InterviewsResponse
import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.model.Question.PossibleAnswer
import io.geekhub.service.questions.web.bean.QuestionRequest
import io.geekhub.service.questions.web.bean.QuestionResponse
import io.geekhub.service.specialization.repository.Specialization
import io.geekhub.service.specialization.web.model.SpecializationRequest
import io.geekhub.service.specialization.web.model.SpecializationResponse


fun QuestionRequest.toEntity(user: ClientUser, spec: Specialization?) = Question(
        question = this.question,
        questionType = this.questionType ?: Question.QuestionType.MULTI_CHOICE,
        clientUser = user,
        specialization = spec,
        jobTitle = this.jobTitle,
        visibility = this.visibility,
        possibleAnswers = this.possibleAnswers.map { it.toEntity() }.toMutableList()
)

fun QuestionRequest.PossibleAnswerRequest.toEntity() = PossibleAnswer(answer = this.answer, correctAnswer = this.correctAnswer)

fun Question.toDTO(liked: Boolean = false) = QuestionResponse(
        this.id.toString(),
        this.question,
        this.questionType,
        this.clientUser.toDTO(),
        this.specialization?.toDTO(),
        this.jobTitle,
        this.possibleAnswers.map { it.toDTO(true) }.toList(),
        this.visibility,
        this.likeCount,
        liked,
        this.deleted,
        this.lastModifiedDate
)

fun PossibleAnswer.toDTO(showAnswer: Boolean) = QuestionResponse.PossibleAnswerResponse(this.answerId, this.answer, if (showAnswer) this.correctAnswer else null)

fun InterviewRequest.toEntity(user: ClientUser, spec: Specialization) = Interview(
        title = this.title,
        description = this.description,
        jobTitle = this.jobTitle,
        clientUser = user,
        specialization = spec,
        visibility = this.visibility,
        defaultDuration = this.defaultDuration
)

fun InterviewRequest.SectionRequest.toEntity() = Interview.Section(
        title = this.title
)

fun InterviewRequest.InterviewQuestionRequest.toEntity(interview: Interview) = Question(
        question = this.question,
        questionType = this.questionType,
        clientUser = interview.clientUser,
        specialization = interview.specialization,
        jobTitle = interview.jobTitle,
        visibility = interview.visibility,
        possibleAnswers = this.possibleAnswers.map { it.toEntity() }.toMutableList()
)

fun InterviewRequest.InterviewQuestionRequest.toSnapshot() = Interview.QuestionSnapshot(
        question = this.question,
        questionType = this.questionType,
        possibleAnswers = this.possibleAnswers.map { it.toEntity() }.toMutableList()
)

fun Interview.toDTO(currentUser: ClientUser): InterviewResponse {
    val showAnswer = currentUser.id == this.clientUser.id

    return InterviewResponse(
            id = this.id.toString(),
            title = this.title,
            description = this.description,
            jobTitle = this.jobTitle,
            clientUser = this.clientUser.toDTO(),
            specialization = this.specialization.toDTO(),
            sections = this.sections.map { it.toDTO(showAnswer) },
            visibility = this.visibility,
            defaultDuration = this.defaultDuration,
            publishedInterviewId = this.latestPublishedInterviewId,
            likeCount = this.likeCount,
            deleted = this.deleted,
            lastModifiedDate = this.lastModifiedDate
    )
}

fun Interview.toLightDTO(likedByClientUser: Boolean = false) = InterviewsResponse.LightInterviewResponse(
        id = this.id.toString(),
        title = this.title,
        description = this.description,
        jobTitle = this.jobTitle,
        clientUser = this.clientUser.toDTO(),
        specialization = this.specialization.toDTO(),
        visibility = this.visibility,
        defaultDuration = this.defaultDuration,
        publishedInterviewId = this.latestPublishedInterviewId,
        likeCount = this.likeCount,
        liked = likedByClientUser,
        interviewSessions = this.groupInterviewSessions().mapValues { it -> it.value.map { it.id.toString() }.toList() }
)

fun Interview.Section.toDTO(showAnswer: Boolean) = InterviewResponse.SectionResponse(
        id = this.id,
        title = this.title,
        questions = this.questions.map { it.toDTO(showAnswer) }.toList()
)

fun Interview.QuestionSnapshot.toDTO(showAnswer: Boolean) = InterviewResponse.QuestionSnapshotResponse(
        id = this.id,
        question = this.question,
        questionType = this.questionType,
        possibleAnswers = this.possibleAnswers.map { it.toDTO(showAnswer) }
)

fun ClientAccount.toDTO() = ClientAccountResponse(
    this.id.toString(),
    this.clientName,
    this.accountType,
    this.planType,
    this.userInvitations
)

fun ClientUser.toDTO(metadata: Map<String, Any>? = mapOf()) = ClientUserResponse(
        id = this.id.toString(),
        email = this.email,
        name = this.name,
        nickname = this.nickname,
        avatar = this.avatar,
        userType = this.userType,
        metadata = metadata
)

fun SpecializationRequest.toEntity() = Specialization(
        name = this.name
)

fun Specialization.toDTO() = SpecializationResponse(
        this.id.toString(),
        this.name
)

