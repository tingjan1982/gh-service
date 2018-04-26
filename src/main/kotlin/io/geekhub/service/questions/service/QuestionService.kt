package io.geekhub.service.questions.service

import io.geekhub.service.questions.model.Question
import java.util.*

interface QuestionService<T : Question<*>> {

    fun saveQuestion(question: T): T

    fun getQuestion(id: String): Optional<T>
}