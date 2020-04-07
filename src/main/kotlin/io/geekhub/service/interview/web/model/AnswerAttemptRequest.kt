package io.geekhub.service.interview.web.model

data class AnswerAttemptRequest(
        val questionId: String,
        val answerId: String?,
        val answer: String?
)
