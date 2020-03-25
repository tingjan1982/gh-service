package io.geekhub.service.questions.web

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.service.ClientAccountService
import io.geekhub.service.questions.service.QuestionSearchService
import io.geekhub.service.questions.service.QuestionService
import io.geekhub.service.questions.service.SocialLikeService
import io.geekhub.service.questions.web.bean.QuestionRequest
import io.geekhub.service.questions.web.bean.QuestionResponse
import io.geekhub.service.questions.web.bean.QuestionsResponse
import io.geekhub.service.shared.extensions.currentUser
import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.shared.extensions.toEntity
import io.geekhub.service.shared.web.filter.ClientAccountFilter.Companion.CLIENT_KEY
import io.geekhub.service.specialization.service.SpecializationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/questions")
class QuestionController(val questionService: QuestionService,
                         val clientAccountService: ClientAccountService,
                         val specializationService: SpecializationService,
                         val questionSearchService: QuestionSearchService,
                         val socialLikeService: SocialLikeService,
                         val serverProperties: ServerProperties) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(QuestionController::class.java)
    }

    @PostMapping
    fun createQuestion(@RequestAttribute(CLIENT_KEY) clientAccount: ClientAccount,
                       @Valid @RequestBody questionRequest: QuestionRequest): QuestionResponse {
        logger.info("Received creation request for: $questionRequest")

        //val clientAccount = resolveClientAccount()
        val specialization = questionRequest.specializationId?.let {
            specializationService.getSpecialization(it)
        }

        val questionToCreate = questionRequest.toEntity(clientAccount, specialization)
        questionRequest.possibleAnswers.forEach {
            questionToCreate.addAnswer(it.toEntity())
        }

        return this.questionService.saveQuestion(questionToCreate).toDTO()
    }

    private fun resolveClientAccount(): ClientAccount {

        val authentication: Authentication = SecurityContextHolder.getContext().authentication

        val principal = authentication.principal
        if (principal is Jwt) {
            val id = principal.claims["sub"] as String

            clientAccountService.getClientAccount(id)?.let {
                return it
            }

            val email = principal.claims["https://api.geekhub.tw/email"] as String
            ClientAccount(id, ClientAccount.AccountType.INDIVIDUAL, email, email).let {
                return clientAccountService.saveClientAccount(it)
            }
        }

        throw RuntimeException("Authentication object is not jwt")
    }

    @GetMapping("/{id}")
    fun getQuestion(@PathVariable id: String): QuestionResponse {
        logger.info("Attempt to lookup question by id: $id")

        return questionService.getQuestion(id).toDTO()
    }

    @GetMapping
    fun listQuestions(@RequestParam(value = "currentPage", defaultValue = "-1") currentPage: Int,
                      @RequestParam(value = "pageSize", defaultValue = "50") pageSize: Int,
                      @RequestParam(value = "page", defaultValue = "0") page: Int,
                      @RequestParam(value = "next", defaultValue = "true") next: Boolean,
                      @RequestParam(value = "sort", defaultValue = "lastModifiedDate") sortField: String): QuestionsResponse {

        val pageToUse: Int = if (next) currentPage + 1 else page
        val pageRequest = PageRequest.of(pageToUse, pageSize, Sort.by(Sort.Order.desc(sortField)))
        this.questionService.getQuestions(resolveClientAccount(), pageRequest).let {

            val nextPage = if (it.hasNext()) pageToUse + 1 else -1
            val prevPage = if (it.hasPrevious()) pageToUse - 1 else -1

            val contextPath = serverProperties.servlet.contextPath
            val nextUrl = "$contextPath/questions?page=${nextPage}&pageSize=${pageSize}"
            val previousUrl = "$contextPath/questions?page=${prevPage}&pageSize=${pageSize}"

            return QuestionsResponse(it.totalElements, it.totalPages, it.size, it.number, it.content, nextUrl, previousUrl)
        }
    }

    @PostMapping("/{id}")
    fun updateQuestion(@PathVariable id: String, @Valid @RequestBody request: QuestionRequest): QuestionResponse {

        questionService.getQuestion(id).let {
            it.question = request.question
            it.possibleAnswers.clear()
            request.possibleAnswers.forEach { ans ->
                it.possibleAnswers.add(ans.toEntity())
            }

            return questionService.saveQuestion(it).toDTO()
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