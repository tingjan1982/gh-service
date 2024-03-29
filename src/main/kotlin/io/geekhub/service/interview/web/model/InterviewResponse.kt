package io.geekhub.service.interview.web.model

import io.geekhub.service.account.web.model.ClientUserResponse
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.web.bean.QuestionResponse
import io.geekhub.service.shared.model.BaseResponse
import io.geekhub.service.shared.model.Visibility
import java.util.*

data class InterviewResponse(
        val id: String,
        val title: String,
        val description: String?,
        val jobTitle: String,
        val clientUser: ClientUserResponse,
        val ownershipType: Interview.OwnershipType,
        val sections: List<SectionResponse>,
        val visibility: Visibility,
        val defaultDuration: Int,
        val releaseResult: Interview.ReleaseResult,
        val publishedInterviewId: String?,
        val likeCount: Long,
        val groupedInterviewSessions: Map<InterviewSession.Status, List<String>>,
        override val deleted: Boolean,
        override val createdDate: Date?,
        override val lastModifiedDate: Date?
) : BaseResponse() {

    data class SectionResponse(
            val id: String,
            val title: String,
            val questions: List<QuestionSnapshotResponse>)

    data class QuestionSnapshotResponse(
            val id: String,
            val question: String,
            val questionType: Question.QuestionType,
            val possibleAnswers: List<QuestionResponse.PossibleAnswerResponse>
    )
}
