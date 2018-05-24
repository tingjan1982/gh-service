package io.geekhub.service.questions.service

import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.model.QuestionAttribute

interface QuestionService {

    fun saveQuestion(question: Question): Question

    fun getQuestion(id: String): Question?

    fun loadQuestion(id: String): Question

    fun createQuestionAnswer(id: String, answer: String): QuestionAttribute
}