package io.geekhub.service.questions.repository

import io.geekhub.service.questions.model.Question
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface QuestionRepository<T : Question<*>> : CrudRepository<T, String> {

    @Query("select q from Question q")
    fun findAllQuestions(): List<Question<*>>
}