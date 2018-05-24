package io.geekhub.service.questions.repository

import io.geekhub.service.questions.model.QuestionAttribute
import org.springframework.data.repository.CrudRepository

interface QuestionAttributeRepository : CrudRepository<QuestionAttribute, Long> {
}