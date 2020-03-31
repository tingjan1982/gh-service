package io.geekhub.service.questions.web.bean

import io.geekhub.service.account.web.model.ClientAccountResponse
import io.geekhub.service.questions.model.Question
import io.geekhub.service.specialization.web.model.SpecializationResponse
import java.util.*

data class QuestionResponse(
        val id: String,
        val question: String,
        val questionType: Question.QuestionType = Question.QuestionType.SHORT_ANSWER,
        val clientAccount: ClientAccountResponse,
        val specialization: SpecializationResponse?,
        val jobTitle: String?,
        val possibleAnswers: List<PossibleAnswerResponse> = listOf(),
        val modifiedDate: Date?
) {
    data class PossibleAnswerResponse(val answer: String, val correct: Boolean)
}
