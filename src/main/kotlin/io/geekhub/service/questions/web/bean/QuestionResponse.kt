package io.geekhub.service.questions.web.bean

import io.geekhub.service.questions.model.Question

data class QuestionResponse(
        val id: String,
        val question: String,
        val category: String,
        val topic: String,
        val difficulty: Question.Difficulty,
        val visibilityScope: Question.VisibilityScope,
        val contributedBy: String?,
        val possibleAnswers: List<PossibleAnswerResponse> = listOf()
) {
    data class PossibleAnswerResponse(val answer: String, val correct: Boolean)
}
