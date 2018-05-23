package io.geekhub.service.questions.service

import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.repository.QuestionRepository
import org.springframework.stereotype.Service
import java.util.*

// TODO: create integration test
@Service
class QuestionServiceImpl(val repository: QuestionRepository) : QuestionService {

    override fun saveQuestion(question: Question): Question {
        return repository.save(question)
    }

    override fun getQuestion(id: String): Optional<Question> {
        return repository.findById(id)
    }
}