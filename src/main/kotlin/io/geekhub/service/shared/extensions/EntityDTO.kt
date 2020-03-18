package io.geekhub.service.shared.extensions

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.web.model.ClientAccountResponse
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

fun ClientAccount.toDTO() = ClientAccountResponse(
        this.id.toString(),
        this.clientName,
        this.email
)