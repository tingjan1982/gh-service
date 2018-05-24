package io.geekhub.service.questions.web.bean

import io.geekhub.service.questions.model.Question

/**
 * category is the domain of the question.
 * Example: Programming, Database, Design pattern.
 *
 * Topic is a further breakdown of areas within a category.
 * Example: Programming -> Java, Database -> SQL
 */
data class QuestionRequest(
        val question: String,
        val category: String,
        val topic: String,
        val difficulty: Question.Difficulty
)