package io.geekhub.service.questions.service

import io.geekhub.service.questions.model.Question

interface QuestionService {

    fun saveQuestion(question: Question): Question

    fun getQuestion(id: String): Question
}