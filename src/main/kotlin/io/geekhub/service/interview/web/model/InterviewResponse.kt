package io.geekhub.service.interview.web.model

import io.geekhub.service.account.web.model.ClientAccountResponse
import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.web.bean.QuestionResponse
import io.geekhub.service.shared.model.Visibility
import io.geekhub.service.specialization.web.model.SpecializationResponse

data class InterviewResponse(
        val id: String,
        val title: String,
        val description: String?,
        val jobTitle: String,
        val clientAccount: ClientAccountResponse,
        val specialization: SpecializationResponse,
        val sections: List<SectionResponse>,
        val visibility: Visibility,
        val latestPublishedInterviewId: String?
) {

    data class SectionResponse(
            val id: String,
            val title: String,
            val questions: List<QuestionSnapshotResponse>)

    data class QuestionSnapshotResponse(
            val id: String,
            val question: String,
            val questionType: Question.QuestionType,
            val possibleAnswers: List<QuestionResponse.PossibleAnswerResponse>,
            val order: String)
}
