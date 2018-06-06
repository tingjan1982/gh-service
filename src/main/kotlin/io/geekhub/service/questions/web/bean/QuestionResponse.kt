package io.geekhub.service.questions.web.bean

import io.geekhub.service.questions.model.Question

data class QuestionResponse(
        val id: String,
        val question: String,
        val category: String,
        val topic: String,
        val difficulty: Question.Difficulty,
        val status: Question.QuestionStatus,
        val visibilityScope: Question.VisibilityScope,
        val contributedBy: String?,
        val answer: String?
)
