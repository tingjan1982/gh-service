package io.geekhub.service.questions.repository

import io.geekhub.service.questions.model.Question
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface QuestionRepository : JpaRepository<Question, String> {

    /**
     * Reference on the use of SpEL for resolving table name:
     * https://docs.spring.io/spring-data/jpa/docs/2.0.7.RELEASE/reference/html/#jpa.query.spel-expressions
     */
    @Query("select q from #{#entityName} q")
    fun findAllQuestions(): List<Question>

    /**
     * Searches questions by question, category and topic, with category and topic on exact match when they have values.
     */
    @Query("select q from #{#entityName} q where q.question like %:searchText% and " +
            "(:category = '' or q.category = :category) and " +
            "(:topic = '' or q.topic = :topic)")
    fun findQuestionsBySearchRequest(searchText: String, category: String, topic: String, page: Pageable): Page<Question>
}
