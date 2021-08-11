package io.geekhub.service.interview.web.model

import io.geekhub.service.interview.model.Interview
import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.web.bean.QuestionRequest
import io.geekhub.service.shared.model.Visibility
import javax.validation.constraints.NotBlank

data class InterviewRequest(
    val ownershipType: OwnershipType = OwnershipType.DEFAULT,
    @field:NotBlank
    val title: String,
    val description: String?,
    @field:NotBlank
    val jobTitle: String,
    val specializationId: String?,
    val visibility: Visibility,
    val defaultDuration: Int = -1,
    val releaseResult: Interview.ReleaseResult = Interview.ReleaseResult.YES,
    val sections: List<SectionRequest> = listOf()
) {
    data class SectionRequest(
        val title: String,
        val questions: List<InterviewQuestionRequest> = listOf()
    )

    data class InterviewQuestionRequest(
        var questionId: String?,
        var id: String?,
        var question: String,
        val questionType: Question.QuestionType = Question.QuestionType.MULTI_CHOICE,
        val possibleAnswers: List<QuestionRequest.PossibleAnswerRequest> = listOf()
    )

    enum class OwnershipType {

        /**
         * User's belonging client account
         */
        DEFAULT,

        /**
         * User's individual client account
         */
        PERSONAL
    }
}
