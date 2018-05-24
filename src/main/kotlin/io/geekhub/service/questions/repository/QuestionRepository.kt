package io.geekhub.service.questions.repository

import io.geekhub.service.questions.model.Question
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface QuestionRepository : JpaRepository<Question, String> {

    /**
     * Reference on the use of SpEL for resolving table name:
     * https://docs.spring.io/spring-data/jpa/docs/2.0.7.RELEASE/reference/html/#jpa.query.spel-expressions
     */
    @Query("select q from #{#entityName} q")
    fun findAllQuestions(): List<Question>
}