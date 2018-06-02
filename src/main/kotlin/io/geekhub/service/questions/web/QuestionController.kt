package io.geekhub.service.questions.web

import io.geekhub.service.questions.model.Answer
import io.geekhub.service.questions.service.QuestionSearchService
import io.geekhub.service.questions.service.QuestionService
import io.geekhub.service.questions.web.bean.AnswerRequest
import io.geekhub.service.questions.web.bean.QuestionRequest
import io.geekhub.service.questions.web.bean.QuestionResponse
import io.geekhub.service.questions.web.bean.SearchRequest
import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.shared.extensions.toEntity
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/questions")
class QuestionController(val questionService: QuestionService, val questionSearchService: QuestionSearchService) {

    companion object {
        val logger = LoggerFactory.getLogger(QuestionController::class.java)!!
    }

    @PostMapping
    fun createQuestion(@RequestBody question: QuestionRequest): QuestionResponse {
        logger.info("Received creation request for: $question")

        val questionToCreate = question.toEntity()
        val createdQuestion = this.questionService.saveQuestion(questionToCreate)

        return createdQuestion.toDTO()
    }

    @GetMapping
    fun searchQuestions(@RequestParam(value = "text", required = false, defaultValue = "") text: String,
                        @RequestParam(value = "category", required = false, defaultValue = "") category: String,
                        @RequestParam(value = "topic", required = false, defaultValue = "") topic: String,
                        page: Pageable): Page<QuestionResponse> {
        
            return this.questionSearchService.searchQuestions(SearchRequest(text, category, topic, page))
                .map { it.toDTO() }
    }

    @GetMapping("/{id}")
    fun getQuestion(@PathVariable id: String): QuestionResponse {
        logger.info("Attempt to lookup question by id: $id")

        return questionService.loadQuestion(id).toDTO()
    }


    @PostMapping("/{id}/answers")
    fun createAnswer(@PathVariable id: String, @RequestBody answerRequest: AnswerRequest): QuestionResponse {

        this.questionService.createQuestionAnswer(id, answerRequest)

        return this.questionService.loadQuestion(id).toDTO()
    }

    @GetMapping("/{id}/answers")
    fun getAnswers(@PathVariable id: String): Answer {

        this.questionService.loadQuestion(id).let {
            return it.getAnswerDetails()
        }
    }
}