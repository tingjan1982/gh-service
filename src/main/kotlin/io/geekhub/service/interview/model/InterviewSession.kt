package io.geekhub.service.interview.model

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.model.BaseMongoObject
import io.geekhub.service.shared.service.data.ClientUserObject
import io.geekhub.service.shared.userkey.UserKeyObject
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
        var currentInterview: Interview,
        @DBRef
        override var clientUser: ClientUser,
        var userEmail: String,
        var name: String? = null,
        @DBRef
        var candidateUser: ClientUser? = null,
        val interviewMode: InterviewMode,
        val duration: Int = -1,
        var status: Status = Status.NOT_STARTED,
        var interviewSentDate: Date? = null,
        var interviewStartDate: Date? = null,
        var interviewEndDate: Date? = null,
        var totalScore: BigDecimal = BigDecimal.ZERO,
        override var userKey: String? = null,
) : BaseMongoObject(), UserKeyObject, ClientUserObject {

    var answerAttemptSections: MutableMap<String, AnswerAttemptSection> = mutableMapOf()

    var followupInterviews: MutableList<FollowUpInterview> = mutableListOf()

    fun addAnswerAttemptSection(answerAttemptSection: AnswerAttemptSection) {
        answerAttemptSections[answerAttemptSection.id] = answerAttemptSection
    }

    data class AnswerAttemptSection(
            val id: String,
            val answerStats: Map<Question.QuestionType, AnswerStats>,
            val answerAttempts: MutableMap<String, QuestionAnswerAttempt> = mutableMapOf()) {

        data class AnswerStats(
                var questionTotal: Int = 0,
                var answered: Int = 0,
                var correct: Int = 0
        )

        fun computeAnswerStats(questionType: Question.QuestionType) {

            val stats = answerAttempts.filter { it.value.questionType == questionType }
                .map { it.value }.let {

                    val answered = it.size
                    val correctAnswers = it.filter { qaa -> qaa.correct == true }.size

                    return@let Pair(answered, correctAnswers)
                }

            answerStats[questionType]?.apply {
                this.answered = stats.first
                this.correct = stats.second
            }
        }
    }

    data class QuestionAnswerAttempt(
        val sectionId: String,
        val questionSnapshotId: String,
        var answerIds: List<String>? = null,
        var answer: String? = null,
        var correct: Boolean? = null
    ) {
        var questionType: Question.QuestionType = if (this.answer != null) {
            Question.QuestionType.SHORT_ANSWER
        } else {
            Question.QuestionType.MULTI_CHOICE
        }
    }

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