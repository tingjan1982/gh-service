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
        clientAccount = account,
        specialization = spec,
        jobTitle = this.jobTitle
)

fun QuestionRequest.PossibleAnswerRequest.toEntity() = PossibleAnswer(answer = this.answer, correctAnswer = this.correctAnswer)

fun Question.toDTO() = QuestionResponse(
        this.questionId.toString(),
        this.question,
        this.clientAccount.toDTO(),
        this.specialization?.name,
        this.jobTitle,
        this.possibleAnswers.map { it.toDTO() }.toList()
)

fun PossibleAnswer.toDTO() = QuestionResponse.PossibleAnswerResponse(this.answer, this.correctAnswer)

fun InterviewRequest.toEntity(account: ClientAccount, spec: Specialization, sections: MutableList<Interview.Section>) = Interview(
        title = this.title,
        description = this.description,
        jobTitle = this.jobTitle,
        clientAccount = account,
        specialization = spec,
        sections = sections
)

fun InterviewRequest.SectionRequest.toEntity() = Interview.Section(
        title = this.title
)

fun Interview.toDTO() = InterviewResponse(
        id = this.id.toString(),
        title = this.title,
        description = this.description,
        jobTitle = this.jobTitle,
        clientAccount = this.clientAccount.toDTO(),
        specialization = this.specialization,
        sections = this.sections.map { it.toDTO() }
)

fun Interview.toLightDTO() = InterviewsResponse.LightInterviewResponse(
        id = this.id.toString(),
        title = this.title,
        description = this.description,
        jobTitle = this.jobTitle,
        clientAccount = this.clientAccount.toDTO(),
        specialization = this.specialization
)

fun Interview.Section.toDTO() = InterviewResponse.SectionResponse(
        title = this.title,
        questions = this.questions
)



fun ClientAccount.toDTO() = ClientAccountResponse(
        this.id.toString(),
        this.clientName,
        this.email
)