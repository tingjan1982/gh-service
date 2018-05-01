package io.geekhub.service.interview.model

import io.geekhub.service.interview.service.InterviewOption
import io.geekhub.service.questions.model.Answer
import io.geekhub.service.questions.model.Question
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Interview() {

    private val maxScore = 100

    constructor(interviewOption: InterviewOption) : this() {
        this.user = interviewOption.user
        this.interviewMode = interviewOption.interviewMode
        this.selectedDuration = interviewOption.duration
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    lateinit var id: String

    var user: String? = null

    var interviewMode: InterviewMode = InterviewMode.MOCK

    /**
     * Default to -1 which indicates unlimited time.
     */
    var selectedDuration: Int = -1

    @Transient
    private val questions: MutableMap<String, Question<*>> = mutableMapOf()

    /**
     * Map key is question ID.
     * Map value is the attempted answer on the question.
     */
    @Transient
    private var answerAttempts: MutableMap<String, Answer<*>> = mutableMapOf()

    /**
     * This stores the computed score of this interview.
     */
    var score: Double? = null

    val startDate: Date = Date()

    var completeDate: Date? = null


    fun addQuestion(question: Question<*>) {
        questions[question.id] = question
    }

    fun addAnswerAttempt(qid: String, answer: Answer<*>) {
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

    private fun calculateQuestionsWeight(): Map<String, Double> {

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