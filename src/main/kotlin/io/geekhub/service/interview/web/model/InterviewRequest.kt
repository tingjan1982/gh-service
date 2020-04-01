package io.geekhub.service.interview.web.model

import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.web.bean.QuestionRequest

data class InterviewRequest(
        val title: String,
        val description: String?,
        val jobTitle: String,
        val specializationId: String,
        val sections: List<SectionRequest> = listOf()
) {
    data class SectionRequest(
            val title: String,
            val questions: List<InterviewQuestionRequest> = listOf()
    )

    data class InterviewQuestionRequest(
            var id: String?,
            var question: String?,
            val questionType: Question.QuestionType = Question.QuestionType.MULTI_CHOICE,
            val possibleAnswers: List<QuestionRequest.PossibleAnswerRequest> = listOf()
    )
}
