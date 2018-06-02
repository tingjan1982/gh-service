package io.geekhub.service.user.web.bean

import io.geekhub.service.questions.web.bean.QuestionResponse

data class UserResponse(
        val id: String,
        val username: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val savedQuestions: List<QuestionResponse> = listOf()
)