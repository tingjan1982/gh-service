package io.geekhub.service.questions.web

import io.geekhub.service.questions.model.Question.QuestionAttribute
import io.geekhub.service.questions.model.Question.QuestionAttribute.Companion.DESCRIPTION_KEY
import io.geekhub.service.questions.service.QuestionSearchService
import io.geekhub.service.questions.service.QuestionService
import io.geekhub.service.questions.service.SocialLikeService
import io.geekhub.service.questions.web.bean.QuestionRequest
import io.geekhub.service.questions.web.bean.QuestionResponse
import io.geekhub.service.questions.web.bean.SearchRequest
import io.geekhub.service.shared.extensions.currentUser
import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.shared.extensions.toEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/questions")
class QuestionController(val questionService: QuestionService, val questionSearchService: QuestionSearchService, val socialLikeService: SocialLikeService) {

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

    @GetMapping("/{id}")
    fun getQuestion(@PathVariable id: String): QuestionResponse {
        logger.info("Attempt to lookup question by id: $id")

        return questionService.getQuestion(id).toDTO()
    }

    @GetMapping
    fun searchQuestions(@RequestParam(value = "text", required = false, defaultValue = "") text: String,
                        @RequestParam(value = "category", required = false, defaultValue = "") category: String,
                        @RequestParam(value = "topic", required = false, defaultValue = "") topic: String,
                        page: Pageable): Page<QuestionResponse> {

            return this.questionSearchService.searchQuestions(SearchRequest(text, category, topic, page))
                .map { it.toDTO() }
    }

    @PutMapping("/{id}/attributes/description")
    fun saveQuestionAttribute(@PathVariable id: String, description: HttpEntity<String>): QuestionResponse {

        return description.body?.let {
            this.questionService.saveOrUpdateAttribute(id, QuestionAttribute(key = DESCRIPTION_KEY, value = it))
            
        }?.toDTO() ?: throw IllegalArgumentException("Description is required")
    }

    @PutMapping("/{id}/like")
    fun likeQuestion(@PathVariable id: String): QuestionResponse {

        val currentUser = SecurityContextHolder.getContext().currentUser()
        this.socialLikeService.likeQuestion(id, currentUser.userId.toString())

        return this.questionService.getQuestion(id).toDTO()
    }
}