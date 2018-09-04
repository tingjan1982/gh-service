package io.geekhub.service.questions.web.bean

import io.geekhub.service.questions.model.Question
import javax.validation.constraints.NotEmpty

/**
 * category is the domain of the question.
 * Example: Programming, Database, Design pattern.
 *
 * Topic is a further breakdown of areas within a category.
 * Example: Programming -> Java, Database -> SQL
 */
data class QuestionRequest(
        @field:NotEmpty val question: String,
        @field:NotEmpty val category: String,
        @field:NotEmpty val topic: String,
        val difficulty: Question.Difficulty = Question.Difficulty.INTERMEDIATE,
        val visibilityScope: Question.VisibilityScope = Question.VisibilityScope.PUBLIC,
        val possibleAnswers: List<PossibleAnswerRequest> = listOf()
) {
    data class PossibleAnswerRequest(@field:NotEmpty val answer: String, @field:NotEmpty val correctAnswer: Boolean)
}