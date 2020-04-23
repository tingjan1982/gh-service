package io.geekhub.service.interview.model

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.model.BaseMongoObject
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.util.*

@Document
data class InterviewSession(
        @Id
        var id: String? = null,
        @DBRef
        val publishedInterview: PublishedInterview,
        @DBRef
        val clientAccount: ClientAccount,
        var userEmail: String,
        var name: String? = null,
        val interviewMode: InterviewMode,
        val duration: Int = -1,
        var status: Status = Status.NOT_STARTED,
        var interviewSentDate: Date? = null,
        var interviewStartDate: Date? = null,
        var interviewEndDate: Date? = null,
        var totalScore: BigDecimal = BigDecimal.ZERO,
        var answerAttemptSections: MutableMap<String, AnswerAttemptSection> = mutableMapOf(),
        val followupInterviews: MutableList<FollowUpInterview> = mutableListOf()

) : BaseMongoObject() {

    data class AnswerAttemptSection(
            val id: String,
            val answerStats: Map<Question.QuestionType, AnswerStats>,
            val answerAttempts: MutableMap<String, QuestionAnswerAttempt> = mutableMapOf()) {

        data class AnswerStats(
                var questionTotal: Int = 0,
                var answered: Int = 0,
                var correct: Int = 0
        )
    }

    data class QuestionAnswerAttempt(
            val sectionId: String,
            val questionSnapshotId: String,
            var answerIds: List<String>? = null,
            var answer: String? = null,
            var correct: Boolean? = null
    )

    data class FollowUpInterview(
            var interviewDate: Date,
            val conductedBy: MutableList<String> = mutableListOf(),
            val followQuestions: MutableList<Question> = mutableListOf(),
            var interviewNote: String? = null,
            var interviewEndDate: Date? = null
    )

    enum class InterviewMode {
        /**
         * Used when user wants to practice the interview.
         */
        MOCK,

        /**
         * Used when company creates an interview session for candidate for actual interview.
         */
        REAL
    }

    enum class Status {
        NOT_STARTED,
        STARTED,
        ENDED
    }
}