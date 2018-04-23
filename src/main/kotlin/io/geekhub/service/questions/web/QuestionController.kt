package io.geekhub.service.questions.web

import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.service.QuestionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/questions")
class QuestionController(private val service: QuestionService) {

    @GetMapping("/{id}")
    fun getQuestion(@PathVariable id: String): Question {
        
        println("Received $id")

        return service.getQuestion(id)
    }
}