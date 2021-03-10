package io.geekhub.service.questions.service

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.model.Question.QuestionAttribute
import io.geekhub.service.shared.model.SearchCriteria
import org.springframework.data.domain.Page

interface QuestionService {

    fun saveQuestion(question: Question): Question

    fun getQuestion(id: String): Question

    fun getQuestions(searchCriteria: SearchCriteria): Page<Question>

    fun getQuestions(clientUser: ClientUser): List<Question>

    /**
     * Save or update attribute in Question and return fully loaded question.
     */
    fun saveOrUpdateAttribute(id: String, questionAttribute: QuestionAttribute): Question

    fun getQuestionAttribute(id: String, key: String): QuestionAttribute?

    fun deleteQuestion(id: String)
}