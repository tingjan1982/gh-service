package io.geekhub.service.questions.web

import io.geekhub.service.questions.model.Answer
import io.geekhub.service.questions.service.QuestionService
import io.geekhub.service.questions.web.bean.AnswerRequest
import io.geekhub.service.questions.web.bean.QuestionRequest
import io.geekhub.service.questions.web.bean.QuestionResponse
import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.shared.extensions.toEntity
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/questions")
class QuestionController(val service: QuestionService) {

    companion object {
        val logger = LoggerFactory.getLogger(QuestionController::class.java)!!
    }

    @PostMapping
    fun createQuestion(@RequestBody question: QuestionRequest): QuestionResponse {
        logger.info("Received creation request for: $question")

        val questionToCreate = question.toEntity()
        val createdQuestion = this.service.saveQuestion(questionToCreate)

        return createdQuestion.toDTO()
    }

    //@PostMapping("/questions/{id}/attributes")

    @GetMapping("/{id}")
    fun getQuestion(@PathVariable id: String): QuestionResponse {
        logger.info("Attempt to lookup question by id: $id")

        return service.loadQuestion(id).toDTO()
    }

    @PostMapping("/{id}/answers")
    fun createAnswer(@PathVariable id: String, @RequestBody answerRequest: AnswerRequest): QuestionResponse {

        this.service.createQuestionAnswer(id, answerRequest)

        return this.service.loadQuestion(id).toDTO()
    }

    @GetMapping("/{id}/answers")
    fun getAnswers(@PathVariable id: String): Answer {

        this.service.loadQuestion(id).let {
            return it.getAnswerDetails()
        }
    }
}