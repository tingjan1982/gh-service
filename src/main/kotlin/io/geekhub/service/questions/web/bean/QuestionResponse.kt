package io.geekhub.service.questions.web.bean

import io.geekhub.service.account.web.model.ClientUserResponse
import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.model.BaseResponse
import io.geekhub.service.shared.model.Visibility
import io.geekhub.service.specialization.web.model.SpecializationResponse
import java.util.*

data class QuestionResponse(
        val id: String,
        val question: String,
        val questionType: Question.QuestionType = Question.QuestionType.SHORT_ANSWER,
        val clientUser: ClientUserResponse,
        val specialization: SpecializationResponse?,
        val jobTitle: String?,
        val possibleAnswers: List<PossibleAnswerResponse> = listOf(),
        val visibility: Visibility,
        val likeCount: Long,
        val liked: Boolean,
        override val deleted: Boolean,
        override val createdDate: Date?,
        override val lastModifiedDate: Date?,
) : BaseResponse() {
    data class PossibleAnswerResponse(val answerId: String, val answer: String, val correctAnswer: Boolean?)
}
