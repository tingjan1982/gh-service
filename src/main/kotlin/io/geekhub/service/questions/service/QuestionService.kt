package io.geekhub.service.questions.service

import io.geekhub.service.questions.model.Question
import java.util.*

interface QuestionService {

    fun saveQuestion(question: Question): Question

    fun getQuestion(id: String): Optional<Question>
}