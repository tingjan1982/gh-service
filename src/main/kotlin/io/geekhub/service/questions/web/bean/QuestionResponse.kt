package io.geekhub.service.questions.web.bean

data class QuestionResponse(
        val id: String,
        val question: String,
        val category: String,
        val topic: String,
        val possibleAnswers: List<PossibleAnswerResponse> = listOf()
) {
    data class PossibleAnswerResponse(val answer: String, val correct: Boolean)
}
