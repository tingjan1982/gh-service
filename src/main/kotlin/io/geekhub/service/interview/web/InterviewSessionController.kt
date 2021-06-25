package io.geekhub.service.interview.web

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.service.InterviewService
import io.geekhub.service.interview.service.InterviewSessionAggregationService
import io.geekhub.service.interview.service.InterviewSessionService
import io.geekhub.service.interview.toDTO
import io.geekhub.service.interview.toEntity
import io.geekhub.service.interview.toLightDTO
import io.geekhub.service.interview.web.model.*
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.model.SearchCriteria
import io.geekhub.service.shared.web.filter.ClientAccountFilter
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import javax.validation.Valid

@RestController
@RequestMapping("/interviewSessions")
class InterviewSessionController(val interviewSessionService: InterviewSessionService,
                                 val interviewService: InterviewService,
                                 val interviewSessionAggregationService: InterviewSessionAggregationService) {


    @PostMapping
    fun createInterviewSession(@RequestAttribute(ClientAccountFilter.CLIENT_USER_KEY) clientUser: ClientUser,
                               @Valid @RequestBody request: InterviewSessionRequest): InterviewSessionResponse {

        interviewService.getPublishedInterviewByInterview(request.interviewId).let {
            return interviewSessionService.createInterviewSession(request.toEntity(it, clientUser)).toDTO(clientUser)
        }
    }

    @GetMapping("/{id}")
    fun getInterviewSession(@RequestAttribute(ClientAccountFilter.CLIENT_USER_KEY) clientUser: ClientUser,
                            @PathVariable id: String): InterviewSessionResponse {

        return interviewSessionService.getInterviewSession(id).toDTO(clientUser)
    }

    @GetMapping("/{id}/averageScore")
    fun getInterviewSessionAverageScore(@RequestAttribute(ClientAccountFilter.CLIENT_USER_KEY) clientUser: ClientUser,
                                        @PathVariable id: String): InterviewSessionAverageStatsResponse {

        interviewSessionService.getInterviewSession(id).let {
            return interviewSessionAggregationService.getAverageScores(it)?.toDTO()
                    ?: throw BusinessException("Unable to compute average stats for this interview session $it")
        }
    }

    @GetMapping
    fun listInterviewSessions(@RequestAttribute(ClientAccountFilter.CLIENT_USER_KEY) clientUser: ClientUser,
                              @RequestParam params: Map<String, String>,
                              @RequestParam("status", required = false) status: InterviewSession.Status?,
                              uriComponentsBuilder: UriComponentsBuilder): InterviewSessionsResponse {

        interviewSessionService.getInterviewSessions(SearchCriteria.fromRequestParameters(clientUser, params), status).let { results ->
            val navigationLinkBuilder = uriComponentsBuilder.path("/interviewSessions").let {
                params.forEach { entry ->
                    it.queryParam(entry.key, entry.value)
                }

                it
            }

            return InterviewSessionsResponse(results.map { it.toLightDTO() }, navigationLinkBuilder)
        }
    }

    @PostMapping("/{id}")
    fun updateInterviewSession(@RequestAttribute(ClientAccountFilter.CLIENT_USER_KEY) clientUser: ClientUser,
                               @PathVariable id: String,
                               @RequestBody updateRequest: UpdateInterviewSessionRequest): InterviewSessionResponse {

        interviewSessionService.getInterviewSession(id).let {
            it.userEmail = updateRequest.userEmail
            it.name = updateRequest.name

            return interviewSessionService.saveInterviewSession(it).toDTO(clientUser)
        }
    }

    @PostMapping("/{id}/send")
    fun sendInterviewSession(@RequestAttribute(ClientAccountFilter.CLIENT_USER_KEY) clientUser: ClientUser,
                             @PathVariable id: String): InterviewSessionResponse {

        interviewSessionService.getInterviewSession(id).let {
            return interviewSessionService.sendInterviewSession(clientUser, it).toDTO(clientUser)
        }
    }

    @PostMapping("/{id}/start")
    fun startInterviewSession(@RequestAttribute(ClientAccountFilter.CLIENT_USER_KEY) clientUser: ClientUser,
                              @PathVariable id: String): InterviewSessionResponse {

        interviewSessionService.getInterviewSession(id).let {
            return interviewSessionService.startInterviewSession(it, clientUser).toDTO(clientUser)
        }
    }

    @PostMapping("/{id}/answers")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun postAnswerAttempt(@RequestAttribute(ClientAccountFilter.CLIENT_USER_KEY) clientUser: ClientUser,
                          @PathVariable id: String,
                          @Valid @RequestBody request: AnswerAttemptRequest) {

        interviewSessionService.getInterviewSession(id).let {
            interviewSessionService.addAnswerAttempt(it, request.toEntity())
        }
    }

    @PostMapping("/{id}/submit")
    fun submitInterviewSession(@RequestAttribute(ClientAccountFilter.CLIENT_USER_KEY) clientUser: ClientUser,
                               @PathVariable id: String): InterviewSessionResponse {

        interviewSessionService.getInterviewSession(id).let {
            return interviewSessionService.submitInterviewSession(it).toDTO(clientUser)
        }
    }

    @PostMapping("/{id}/markQuestion")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun markQuestion(@RequestAttribute(ClientAccountFilter.CLIENT_USER_KEY) clientUser: ClientUser,
                     @PathVariable id: String,
                     @Valid @RequestBody request: MarkAnswerRequest) {

        interviewSessionService.getInterviewSession(id).let {
            interviewSessionService.markInterviewSessionAnswer(it, request.sectionId, request.questionSnapshotId, request.correct)
        }
    }

    @PostMapping("/{id}/calculateScore")
    fun calculateScore(@RequestAttribute(ClientAccountFilter.CLIENT_USER_KEY) clientUser: ClientUser,
                       @PathVariable id: String): InterviewSessionResponse {

        interviewSessionService.getInterviewSession(id).let {
            return interviewSessionService.calculateScore(it).toDTO(clientUser)
        }
    }
}