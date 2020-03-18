package io.geekhub.service.questions.web.bean

import io.geekhub.service.account.web.model.ClientAccountResponse

data class QuestionResponse(
        val id: String,
        val question: String,
        val clientAccount: ClientAccountResponse,
        val specialization: String?,
        val jobTitle: String?,
        val possibleAnswers: List<PossibleAnswerResponse> = listOf()
) {
    data class PossibleAnswerResponse(val answer: String, val correct: Boolean)
}
