package io.geekhub.service.questions.service

import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.model.QuestionAttribute

interface QuestionService {

    fun saveQuestion(question: Question): Question

    fun getQuestion(id: String): Question?

    fun loadQuestion(id: String): Question

    fun updateVisibility(id: String, visibilityToChange: Question.VisibilityScope): Question

    /**
     * Save or update attribute in Question and return fully loaded question.
     */
    fun saveOrUpdateAttribute(id: String, questionAttribute: QuestionAttribute): Question
}