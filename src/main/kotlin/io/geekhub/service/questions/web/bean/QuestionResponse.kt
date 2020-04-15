package io.geekhub.service.questions.web.bean

import io.geekhub.service.account.web.model.ClientAccountResponse
import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.model.Visibility
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
        val visibility: Visibility,
        val modifiedDate: Date?
) {
    data class PossibleAnswerResponse(val answerId: String, val answer: String, val correctAnswer: Boolean?)
}
