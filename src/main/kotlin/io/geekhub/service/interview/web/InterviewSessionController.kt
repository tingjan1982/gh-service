package io.geekhub.service.interview.web

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.service.InterviewService
import io.geekhub.service.interview.service.InterviewSessionService
import io.geekhub.service.interview.toDTO
import io.geekhub.service.interview.toEntity
import io.geekhub.service.interview.toLightDTO
import io.geekhub.service.interview.web.model.*
import io.geekhub.service.shared.model.SearchCriteria
import io.geekhub.service.shared.model.Visibility
import io.geekhub.service.shared.web.filter.ClientAccountFilter
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/interviewSessions")
class InterviewSessionController(val interviewSessionService: InterviewSessionService,
                                 val interviewService: InterviewService,
                                 val serverProperties: ServerProperties) {


    @PostMapping
    fun createInterviewSession(@RequestAttribute(ClientAccountFilter.CLIENT_KEY) clientAccount: ClientAccount,
                               @Valid @RequestBody request: InterviewSessionRequest): InterviewSessionResponse {

        interviewService.getPublishedInterviewByInterview(request.interviewId).let {
            return interviewSessionService.saveInterviewSession(request.toEntity(it, clientAccount)).toDTO(true)
        }
    }

    @GetMapping("/{id}")
    fun getInterviewSession(@RequestAttribute(ClientAccountFilter.CLIENT_KEY) clientAccount: ClientAccount,
                            @PathVariable id: String): InterviewSessionResponse {

        return interviewSessionService.getInterviewSession(id).let {
            it.toDTO(showCorrectAnswer(it, clientAccount))
        }
    }

    @GetMapping
    fun listInterviewSessions(@RequestAttribute(ClientAccountFilter.CLIENT_KEY) clientAccount: ClientAccount,
                              @RequestParam params: Map<String, String>): InterviewSessionsResponse {

        interviewSessionService.getInterviewSessions(SearchCriteria.fromRequestParameters(clientAccount, params)).let { results ->
            val contextPath = serverProperties.servlet.contextPath
            return InterviewSessionsResponse(results.map { it.toLightDTO() }, contextPath, "interviewSessions")
        }
    }

    @PostMapping("/{id}/send")
    fun sendInterviewSession(@RequestAttribute(ClientAccountFilter.CLIENT_KEY) clientAccount: ClientAccount,
                             @PathVariable id: String): InterviewSessionResponse {

        interviewSessionService.getInterviewSession(id).let {
            return interviewSessionService.sendInterviewSession(it).toDTO(showCorrectAnswer(it, clientAccount))
        }
    }

    @PostMapping("/{id}/start")
    fun startInterviewSession(@RequestAttribute(ClientAccountFilter.CLIENT_KEY) clientAccount: ClientAccount,
                              @PathVariable id: String): InterviewSessionResponse {

        interviewSessionService.getInterviewSession(id).let {
            return interviewSessionService.startInterviewSession(it).toDTO(showCorrectAnswer(it, clientAccount))
        }
    }

    @PostMapping("/{id}/answers")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun postAnswerAttempt(@RequestAttribute(ClientAccountFilter.CLIENT_KEY) clientAccount: ClientAccount,
                          @PathVariable id: String,
                          @Valid @RequestBody request: AnswerAttemptRequest) {

        interviewSessionService.getInterviewSession(id).let {
            interviewSessionService.addAnswerAttempt(it, request.toEntity())
        }
    }

    @PostMapping("/{id}/submit")
    fun submitInterviewSession(@RequestAttribute(ClientAccountFilter.CLIENT_KEY) clientAccount: ClientAccount,
                               @PathVariable id: String): InterviewSessionResponse {

        interviewSessionService.getInterviewSession(id).let {
            return interviewSessionService.submitInterviewSession(it).toDTO(showCorrectAnswer(it, clientAccount))
        }
    }

    @PostMapping("/{id}/markQuestion")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun markQuestion(@RequestAttribute(ClientAccountFilter.CLIENT_KEY) clientAccount: ClientAccount,
                     @PathVariable id: String,
                     @Valid @RequestBody request: MarkAnswerRequest) {

        interviewSessionService.getInterviewSession(id).let {
            interviewSessionService.markInterviewSessionAnswer(it, request.sectionId, request.questionSnapshotId, request.correct)
        }
    }

    @PostMapping("/{id}/calculateScore")
    fun calculateScore(@RequestAttribute(ClientAccountFilter.CLIENT_KEY) clientAccount: ClientAccount,
                       @PathVariable id: String): InterviewSessionResponse {

        interviewSessionService.calculateScore(id).let {
            return it.toDTO(showCorrectAnswer(it, clientAccount))
        }
    }

    private fun showCorrectAnswer(interviewSession: InterviewSession, currentAccount: ClientAccount): Boolean {

        interviewSession.publishedInterview.referencedInterview.let {
            val publicInterview = it.visibility == Visibility.PUBLIC
            val interviewOwner = it.clientAccount.id == currentAccount.id

            return publicInterview || interviewOwner
        }
    }
}