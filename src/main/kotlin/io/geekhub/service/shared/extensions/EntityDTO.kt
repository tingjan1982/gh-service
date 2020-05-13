package io.geekhub.service.shared.extensions

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.web.model.ClientAccountResponse
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
import io.geekhub.service.user.model.User
import io.geekhub.service.user.web.bean.UserRequest
import io.geekhub.service.user.web.bean.UserResponse

fun UserRequest.toEntity() = User(
        username = this.username,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email
)

fun User.toDTO() = UserResponse(
        this.userId.toString(),
        this.username,
        this.firstName,
        this.lastName,
        this.email,
        this.savedQuestions.map { it.value.toDTO() }
)

fun QuestionRequest.toEntity(account: ClientAccount, spec: Specialization?) = Question(
        question = this.question,
        questionType = this.questionType ?: Question.QuestionType.MULTI_CHOICE,
        clientAccount = account,
        specialization = spec,
        jobTitle = this.jobTitle,
        visibility = this.visibility,
        possibleAnswers = this.possibleAnswers.map { it.toEntity() }.toMutableList()
)

fun QuestionRequest.PossibleAnswerRequest.toEntity() = PossibleAnswer(answer = this.answer, correctAnswer = this.correctAnswer)

fun Question.toDTO() = QuestionResponse(
        this.id.toString(),
        this.question,
        this.questionType,
        this.clientAccount.toDTO(),
        this.specialization?.toDTO(),
        this.jobTitle,
        this.possibleAnswers.map { it.toDTO(true) }.toList(),
        this.visibility,
        this.likeCount,
        this.deleted,
        this.lastModifiedDate
)

fun PossibleAnswer.toDTO(showAnswer: Boolean) = QuestionResponse.PossibleAnswerResponse(this.answerId, this.answer, if (showAnswer) this.correctAnswer else null)

fun InterviewRequest.toEntity(account: ClientAccount, spec: Specialization) = Interview(
        title = this.title,
        description = this.description,
        jobTitle = this.jobTitle,
        clientAccount = account,
        specialization = spec,
        visibility = this.visibility
)

fun InterviewRequest.SectionRequest.toEntity() = Interview.Section(
        title = this.title
)

fun InterviewRequest.InterviewQuestionRequest.toEntity(interview: Interview) = Question(
        question = this.question,
        questionType = this.questionType,
        clientAccount = interview.clientAccount,
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

fun Interview.toDTO(showAnswer: Boolean = true) = InterviewResponse(
        id = this.id.toString(),
        title = this.title,
        description = this.description,
        jobTitle = this.jobTitle,
        clientAccount = this.clientAccount.toDTO(),
        specialization = this.specialization.toDTO(),
        sections = this.sections.map { it.toDTO(showAnswer) },
        visibility = this.visibility,
        publishedInterviewId = this.latestPublishedInterviewId,
        likeCount = this.likeCount,
        deleted = this.deleted,
        lastModifiedDate = this.lastModifiedDate
)

fun Interview.toLightDTO() = InterviewsResponse.LightInterviewResponse(
        id = this.id.toString(),
        title = this.title,
        description = this.description,
        jobTitle = this.jobTitle,
        clientAccount = this.clientAccount.toDTO(),
        specialization = this.specialization.toDTO(),
        visibility = this.visibility,
        publishedInterviewId = this.latestPublishedInterviewId,
        likeCount = this.likeCount
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

fun ClientAccount.toDTO() = ClientAccountResponse(this.id.toString(), this.clientName, this.email)

fun SpecializationRequest.toEntity() = Specialization(
        name = this.name
)

fun Specialization.toDTO() = SpecializationResponse(
        this.id.toString(),
        this.name
)

