package io.geekhub.service.interview.model

import io.geekhub.service.questions.model.Answer
import io.geekhub.service.questions.model.Question
import io.geekhub.service.user.model.User
import java.util.*
import javax.persistence.*

@Entity(name = "gh_interview")
@SequenceGenerator(name = "interview_sequence", sequenceName = "interview_sequence")
data class Interview(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "interview_sequence")
        var id: Long = 0,
        @ManyToOne
        var user: User) {

    private val maxScore = 100

    var interviewMode: InterviewMode = InterviewMode.MOCK

    /**
     * Default to -1 which indicates unlimited time.
     */
    var selectedDuration: Int = -1

    @Transient
    private val questions: MutableMap<Long, Question<*>> = mutableMapOf()

    /**
     * Map key is question ID.
     * Map value is the attempted answer on the question.
     */
    @Transient
    private var answerAttempts: MutableMap<Long, Answer<*>> = mutableMapOf()

    /**
     * This stores the computed score of this interview.
     */
    var score: Double? = null

    val startDate: Date = Date()

    var completeDate: Date? = null


    fun addQuestion(question: Question<*>) {
        questions[question.id] = question
    }

    fun addAnswerAttempt(qid: Long, answer: Answer<*>) {
        this.answerAttempts[qid] = answer
    }

    fun questionsCount(): Int {
        return questions.size
    }

    /**
     * Main method
     */
    fun assessInterview() {
        val computedScore = this.computeScore()
        this.score = computedScore

        completeDate = Date()
    }

    /**
     * Computes the score of this interview taking into the questions' weight.
     */
    internal fun computeScore(): Double {

        var score = 0.0

        if (questions.isNotEmpty() && answerAttempts.isNotEmpty()) {
            val weightsMap = this.calculateQuestionsWeight()

            score = answerAttempts.filter {
                this.isCorrectAnswer(this.questions[it.key]!!, it.value)
                //this.questions[it.key]!!.isCorrectAnswer(it.value)

            }.map {
                weightsMap[it.key]!!

            }.sumByDouble {
                score + it
            }

        }

        return score
    }

    private fun calculateQuestionsWeight(): Map<Long, Double> {

        val averageWeight = maxScore / this.questions.size

        return this.questions.map {
            it.key to it.value.weight * averageWeight
        }.toMap()
    }

    private fun isCorrectAnswer(q: Question<*>, a: Answer<*>): Boolean {
        return q.answer == a.getAnswer()
    }


    enum class InterviewMode {
        MOCK, REAL
    }
}