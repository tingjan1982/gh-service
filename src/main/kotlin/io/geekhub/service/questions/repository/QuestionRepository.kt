package io.geekhub.service.questions.repository

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.questions.model.Question
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.query.TextCriteria
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository

interface QuestionRepository : PagingAndSortingRepository<Question, String> {

    fun findAllBy(textCriteria: TextCriteria, page: Pageable): Page<Question>

    fun findAllByClientAccount(clientAccount: ClientAccount, page: Pageable): Page<Question>

    fun findAllByClientAccount(clientAccount: ClientAccount, textCriteria: TextCriteria, page: Pageable): Page<Question>

    /**
     * Searches questions by question, category and topic, with category and topic on exact match when they have values.
     */
    @Query("select q from #{#entityName} q where q.question like %:searchText% and " +
            "(:category = '' or q.category = :category) and " +
            "(:topic = '' or q.topic = :topic)")
    fun findQuestionsBySearchRequest(searchText: String, category: String, topic: String, page: Pageable): Page<Question>
}
