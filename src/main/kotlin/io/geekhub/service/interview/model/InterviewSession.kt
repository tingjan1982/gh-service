package io.geekhub.service.interview.model

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.model.BaseMongoObject
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
data class InterviewSession(
        @Id
        var id: String? = null,
        @DBRef
        val publishedInterview: PublishedInterview,
        @DBRef
        val clientAccount: ClientAccount,
        val userEmail: String,
        val name: String? = null,
        val interviewMode: InterviewMode,
        val duration: Int = -1,
        var interviewSentDate: Date? = null,
        var interviewStartDate: Date? = null,
        var interviewEndDate: Date? = null,
        var score: Double = 0.0,
        val answerAttempts: MutableMap<String, QuestionAnswerAttempt> = mutableMapOf(),
        val followupInterviews: MutableList<FollowUpInterview> = mutableListOf()

) : BaseMongoObject() {

    data class QuestionAnswerAttempt(
            val answerId: String? = null,
            val answer: String? = null
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
}