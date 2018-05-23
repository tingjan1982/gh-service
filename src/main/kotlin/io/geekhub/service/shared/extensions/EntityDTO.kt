package io.geekhub.service.shared.extensions

import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.web.bean.QuestionRequest
import io.geekhub.service.questions.web.bean.QuestionResponse
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
        this.id.toString(),
        this.username,
        this.firstName,
        this.lastName,
        this.email
)

fun QuestionRequest.toEntity() = Question(
        question = this.question,
        category = this.category,
        topic = this.topic,
        difficulty = this.difficulty
)

fun Question.toDTO() = QuestionResponse(
        this.questionId.toString(),
        this.question,
        this.category,
        this.topic,
        this.difficulty
)
