package io.geekhub.service.questions.web

import io.geekhub.service.questions.service.QuestionService
import io.geekhub.service.questions.web.bean.QuestionRequest
import io.geekhub.service.questions.web.bean.QuestionResponse
import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.shared.extensions.toEntity
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import javax.persistence.EntityNotFoundException

@RestController
@RequestMapping("/questions")
class QuestionController(val service: QuestionService) {

    companion object {
        val logger = LoggerFactory.getLogger(QuestionController::class.java)!!
    }

    @PostMapping
    fun createQues(@RequestBody question: QuestionRequest): QuestionResponse {
        logger.info("Received creation request for: $question")

        val questionToCreate = question.toEntity()
        val createdQuestion = this.service.saveQuestion(questionToCreate)

        return createdQuestion.toDTO()
    }

    //@PostMapping("/questions/{id}/attributes")
    //@PostMapping("/questions/{id}/answers")

    @GetMapping("/{id}")
    fun getQuestion(@PathVariable id: String): QuestionResponse {
        logger.info("Attempt to lookup question by id: $id")

        return service.getQuestion(id).orElseThrow({ EntityNotFoundException("not found") }).toDTO()
    }
}