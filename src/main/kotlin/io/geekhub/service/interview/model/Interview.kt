package io.geekhub.service.interview.model

import io.geekhub.service.questions.model.Question
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
data class Interview(
        @Id
        var id: String? = null) {

    private val maxScore = 100

    @DBRef
    private val questions: MutableList<Question> = mutableListOf()

    /**
     * This stores the computed score of this interview.
     */
    var score: Double? = null

    val startDate: Date = Date()

    var completeDate: Date? = null


    fun addQuestion(question: Question) {
        questions.add(question)
    }

    fun getQuestions(): Map<String, Question> {
        return this.questions.map {
            Pair(it.questionId.toString(), it)
        }.toMap()
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
        val weightsMap = this.calculateQuestionsWeight()

        // todo: revisit interview score calculation
//        score = questions.filter {
//            it.value.answer == it.key.getAnswer()
//        }.map {
//            weightsMap[it.key.id]
//        }.sumByDouble {
//            score + it!!
//        }

        return score
    }

    private fun calculateQuestionsWeight(): Map<String, Double> {

        val averageWeight = maxScore / this.questions.size

//        return this.questions.map {
//            it.questionId.toString() to it.weight * averageWeight
//        }.toMap()

        return mapOf()
    }

}