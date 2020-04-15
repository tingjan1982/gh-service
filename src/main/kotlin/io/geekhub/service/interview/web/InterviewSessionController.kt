package io.geekhub.service.interview.web

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.service.InterviewService
import io.geekhub.service.interview.service.InterviewSessionService
import io.geekhub.service.interview.toDTO
import io.geekhub.service.interview.toEntity
import io.geekhub.service.interview.toLightDTO
import io.geekhub.service.interview.web.model.AnswerAttemptRequest
import io.geekhub.service.interview.web.model.InterviewSessionRequest
import io.geekhub.service.interview.web.model.InterviewSessionResponse
import io.geekhub.service.interview.web.model.InterviewSessionsResponse
import io.geekhub.service.shared.model.SearchCriteria
import io.geekhub.service.shared.web.filter.ClientAccountFilter
import org.springframework.boot.autoconfigure.web.ServerProperties
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
            return interviewSessionService.saveInterviewSession(request.toEntity(it, clientAccount)).toDTO()
        }
    }

    @GetMapping("/{id}")
    fun getInterviewSession(@PathVariable id: String): InterviewSessionResponse {

        return interviewSessionService.getInterviewSession(id).toDTO()
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
            return interviewSessionService.sendInterviewSession(it).toDTO()
        }
    }

    @PostMapping("/{id}/start")
    fun startInterviewSession(@RequestAttribute(ClientAccountFilter.CLIENT_KEY) clientAccount: ClientAccount,
                              @PathVariable id: String): InterviewSessionResponse {

        interviewSessionService.getInterviewSession(id).let {
            return interviewSessionService.startInterviewSession(it).toDTO()
        }
    }

    @PostMapping("/{id}/answers")
    fun postAnswerAttempt(@RequestAttribute(ClientAccountFilter.CLIENT_KEY) clientAccount: ClientAccount,
                          @PathVariable id: String,
                          @Valid @RequestBody request: AnswerAttemptRequest): InterviewSessionResponse {

        interviewSessionService.getInterviewSession(id).let {
            return interviewSessionService.addAnswerAttempt(it, request.questionId, request.toEntity()).toDTO()
        }
    }

    @PostMapping("/{id}/submit")
    fun submitInterviewSession(@RequestAttribute(ClientAccountFilter.CLIENT_KEY) clientAccount: ClientAccount,
                               @PathVariable id: String): InterviewSessionResponse {

        interviewSessionService.getInterviewSession(id).let {
            return interviewSessionService.submitInterviewSession(it).toDTO()
        }
    }
}