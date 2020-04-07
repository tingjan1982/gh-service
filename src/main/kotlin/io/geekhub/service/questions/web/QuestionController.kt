package io.geekhub.service.questions.web

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.questions.service.QuestionService
import io.geekhub.service.questions.service.SocialLikeService
import io.geekhub.service.questions.web.bean.QuestionRequest
import io.geekhub.service.questions.web.bean.QuestionResponse
import io.geekhub.service.questions.web.bean.QuestionsResponse
import io.geekhub.service.shared.extensions.currentUser
import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.shared.extensions.toEntity
import io.geekhub.service.shared.model.SearchCriteria
import io.geekhub.service.shared.web.filter.ClientAccountFilter.Companion.CLIENT_KEY
import io.geekhub.service.specialization.service.SpecializationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/questions")
class QuestionController(val questionService: QuestionService,
                         val specializationService: SpecializationService,
                         val socialLikeService: SocialLikeService,
                         val serverProperties: ServerProperties) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(QuestionController::class.java)
    }

    @PostMapping
    fun createQuestion(@RequestAttribute(CLIENT_KEY) clientAccount: ClientAccount,
                       @Valid @RequestBody questionRequest: QuestionRequest): QuestionResponse {
        logger.info("Received creation request for: $questionRequest")

        val specialization = questionRequest.specializationId?.let {
            specializationService.getSpecialization(it)
        }

        val questionToCreate = questionRequest.toEntity(clientAccount, specialization)
        
        return this.questionService.saveQuestion(questionToCreate).toDTO()
    }

    @GetMapping("/{id}")
    fun getQuestion(@PathVariable id: String): QuestionResponse {
        logger.info("Attempt to lookup question by id: $id")

        return questionService.getQuestion(id).toDTO()
    }

    @GetMapping
    fun listQuestions(@RequestAttribute(CLIENT_KEY) clientAccount: ClientAccount,
                      @RequestParam(value = "owner", defaultValue = "true") owner: Boolean,
                      @RequestParam(value = "keyword", required = false) keyword: String?,
                      @RequestParam(value = "currentPage", defaultValue = "-1") currentPage: Int,
                      @RequestParam(value = "next", defaultValue = "true") next: Boolean,
                      @RequestParam(value = "page", defaultValue = "0") page: Int,
                      @RequestParam(value = "pageSize", defaultValue = "50") pageSize: Int,
                      @RequestParam(value = "sort", defaultValue = "lastModifiedDate") sortField: String): QuestionsResponse {

        val pageToUse: Int = if (next) currentPage + 1 else page
        val pageRequest = PageRequest.of(pageToUse, pageSize, Sort.by(Sort.Order.desc(sortField)))
        val decoratedKeyword = if (keyword.isNullOrEmpty()) null else keyword
        val searchCriteria = SearchCriteria(owner, clientAccount, decoratedKeyword, pageRequest)

        this.questionService.getQuestions(searchCriteria).let { result ->
            val contextPath = serverProperties.servlet.contextPath
            return QuestionsResponse(result.map { it.toDTO() }, contextPath, "questions")
        }
    }

    @PostMapping("/{id}")
    fun updateQuestion(@PathVariable id: String, @Valid @RequestBody request: QuestionRequest): QuestionResponse {

        questionService.getQuestion(id).let { q ->
            q.question = request.question
            q.possibleAnswers.clear()
            q.possibleAnswers = request.possibleAnswers.map { it.toEntity() }.toMutableList()

            return questionService.saveQuestion(q).toDTO()
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun deleteQuestion(@PathVariable id: String) {

        questionService.deleteQuestion(id)
    }

    @PutMapping("/{id}/like")
    fun likeQuestion(@PathVariable id: String): QuestionResponse {

        val currentUser = SecurityContextHolder.getContext().currentUser()
        this.socialLikeService.likeQuestion(id, currentUser.userId.toString())

        return this.questionService.getQuestion(id).toDTO()
    }
}