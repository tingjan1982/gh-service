package io.geekhub.service.questions.web.bean

import io.geekhub.service.account.web.model.ClientAccountResponse
import io.geekhub.service.specialization.web.model.SpecializationResponse

data class QuestionResponse(
        val id: String,
        val question: String,
        val clientAccount: ClientAccountResponse,
        val specialization: SpecializationResponse?,
        val jobTitle: String?,
        val possibleAnswers: List<PossibleAnswerResponse> = listOf()
) {
    data class PossibleAnswerResponse(val answer: String, val correct: Boolean)
}
