package io.geekhub.service.questions.web

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.service.ClientAccountService
import io.geekhub.service.questions.model.Question.QuestionAttribute
import io.geekhub.service.questions.model.Question.QuestionAttribute.Companion.DESCRIPTION_KEY
import io.geekhub.service.questions.service.QuestionSearchService
import io.geekhub.service.questions.service.QuestionService
import io.geekhub.service.questions.service.SocialLikeService
import io.geekhub.service.questions.web.bean.QuestionRequest
import io.geekhub.service.questions.web.bean.QuestionResponse
import io.geekhub.service.shared.extensions.currentUser
import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.shared.extensions.toEntity
import io.geekhub.service.specialization.service.SpecializationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
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
                         val socialLikeService: SocialLikeService) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(QuestionController::class.java)
    }

    @PostMapping
    fun createQuestion(@Valid @RequestBody questionRequest: QuestionRequest): QuestionResponse {
        logger.info("Received creation request for: $questionRequest")

        val clientAccount = resolveClientAccount()
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
    fun listQuestions(): List<QuestionResponse> {
        return this.questionService.getQuestions(resolveClientAccount())
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