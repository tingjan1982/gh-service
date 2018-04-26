package io.geekhub.service.questions.service

import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.repository.QuestionRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class QuestionServiceImpl<T : Question>(private val repository: QuestionRepository<T>) : QuestionService<T> {

    override fun saveQuestion(question: T): T {
        return repository.save(question)
    }

    override fun getQuestion(id: String): Optional<T> {
        return repository.findById(id)
    }
}