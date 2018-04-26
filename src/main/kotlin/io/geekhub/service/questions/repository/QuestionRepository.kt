package io.geekhub.service.questions.repository

import io.geekhub.service.questions.model.Question
import org.springframework.data.repository.CrudRepository

interface QuestionRepository<T : Question<*>> : CrudRepository<T, String>