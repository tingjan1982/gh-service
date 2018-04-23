package io.geekhub.service.questions.service

import io.geekhub.service.questions.model.Question
import org.springframework.stereotype.Service

@Service
class QuestionServiceImpl : QuestionService {
    override fun saveQuestion(question: Question): Question {

        println("Save called")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getQuestion(id: String): Question {

        println("Get called")
        val question = Question()
        question.id = id

        return question
    }
}