package io.geekhub.service.questions.service

import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.model.Question.QuestionAttribute
import io.geekhub.service.shared.model.SearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

interface QuestionService {

    fun saveQuestion(question: Question): Question

    fun getQuestion(id: String): Question

    fun getQuestions(searchCriteria: SearchCriteria, pageRequest: PageRequest): Page<Question>

    /**
     * Save or update attribute in Question and return fully loaded question.
     */
    fun saveOrUpdateAttribute(id: String, questionAttribute: QuestionAttribute): Question

    fun getQuestionAttribute(id: String, key: String): QuestionAttribute?

    fun deleteQuestion(id: String)
}