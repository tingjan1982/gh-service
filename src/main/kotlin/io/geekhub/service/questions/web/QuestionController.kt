package io.geekhub.service.questions.web

import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.service.QuestionSearchService
import io.geekhub.service.questions.service.QuestionService
import io.geekhub.service.questions.web.bean.QuestionRequest
import io.geekhub.service.questions.web.bean.QuestionResponse
import io.geekhub.service.questions.web.bean.SearchRequest
import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.shared.extensions.toEntity
import io.geekhub.service.user.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/questions")
class QuestionController(val questionService: QuestionService, val questionSearchService: QuestionSearchService, val userService: UserService) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(QuestionController::class.java)
    }

    @PostMapping
    fun createQuestion(@Valid @RequestBody question: QuestionRequest): QuestionResponse {
        logger.info("Received creation request for: $question")

        val questionToCreate = question.toEntity()
        question.possibleAnswers.forEach {
            questionToCreate.addAnswer(it.toEntity())
        }

        return this.questionService.saveQuestion(questionToCreate).toDTO()
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


    @PutMapping("/{id}/visibility")
    fun changeStatus(@PathVariable id: String, visibility: HttpEntity<String>): QuestionResponse {

        visibility.body?.let {
            return this.questionService.updateVisibility(id, Question.VisibilityScope.valueOf(it)).toDTO()

        } ?: throw IllegalArgumentException("Visibility is required")

    }
}