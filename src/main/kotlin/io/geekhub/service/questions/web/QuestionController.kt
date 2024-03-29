package io.geekhub.service.questions.web

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.likes.service.LikeService
import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.service.QuestionService
import io.geekhub.service.questions.web.bean.QuestionRequest
import io.geekhub.service.questions.web.bean.QuestionResponse
import io.geekhub.service.questions.web.bean.QuestionsResponse
import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.shared.extensions.toEntity
import io.geekhub.service.shared.model.SearchCriteria
import io.geekhub.service.shared.web.filter.ClientAccountFilter.Companion.CLIENT_USER_KEY
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import javax.validation.Valid

@RestController
@RequestMapping("/questions")
class QuestionController(val questionService: QuestionService,
                         val likeService: LikeService) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(QuestionController::class.java)
    }

    @PostMapping
    fun createQuestion(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                       @Valid @RequestBody questionRequest: QuestionRequest): QuestionResponse {
        logger.info("Received creation request for: $questionRequest")

        val questionToCreate = questionRequest.toEntity(clientUser)

        return this.questionService.saveQuestion(questionToCreate).toDTO()
    }

    @GetMapping("/{id}")
    fun getQuestion(@PathVariable id: String): QuestionResponse {
        logger.info("Attempt to lookup question by id: $id")

        return questionService.getQuestion(id).toDTO()
    }

    @GetMapping
    fun listQuestions(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                      @RequestParam map: Map<String, String>,
                      uriComponentsBuilder: UriComponentsBuilder): QuestionsResponse {

        this.questionService.getQuestions(SearchCriteria.fromRequestParameters(clientUser, map)).let { result ->
            val navigationLinkBuilder = uriComponentsBuilder.path("/questions").let {
                map.forEach { entry ->
                    it.queryParam(entry.key, entry.value)
                }

                it
            }

            val likedQuestions = likeService.getLikedObjects(clientUser, Question::class).map { it.objectId }.toList()

            return QuestionsResponse(result.map { it.toDTO(likedQuestions.contains(it.id.toString())) }, navigationLinkBuilder)
        }
    }

    @PostMapping("/{id}")
    fun updateQuestion(@PathVariable id: String,
                       @Valid @RequestBody request: QuestionRequest): QuestionResponse {

        questionService.getQuestion(id).let { q ->
            q.question = request.question
            request.questionType?.let {
                q.questionType = it
            }

            q.jobTitle = request.jobTitle
            q.visibility = request.visibility

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

    @PostMapping("/{id}/like")
    fun likeQuestion(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                     @PathVariable id: String): QuestionResponse {

        questionService.getQuestion(id).let {
            likeService.like(clientUser, it)

            return questionService.getQuestion(id).toDTO()
        }
    }

    @PostMapping("/{id}/unlike")
    fun unlikeQuestion(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                       @PathVariable id: String): QuestionResponse {

        questionService.getQuestion(id).let {
            likeService.unlike(clientUser, it)

            return questionService.getQuestion(id).toDTO()
        }
    }
}