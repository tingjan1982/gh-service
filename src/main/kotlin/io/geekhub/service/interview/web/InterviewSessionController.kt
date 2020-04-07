package io.geekhub.service.interview.web

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.service.InterviewService
import io.geekhub.service.interview.service.InterviewSessionService
import io.geekhub.service.interview.toDTO
import io.geekhub.service.interview.toEntity
import io.geekhub.service.interview.web.model.AnswerAttemptRequest
import io.geekhub.service.interview.web.model.InterviewSessionRequest
import io.geekhub.service.interview.web.model.InterviewSessionResponse
import io.geekhub.service.notification.service.NotificationService
import io.geekhub.service.shared.web.filter.ClientAccountFilter
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/interviewSessions")
class InterviewSessionController(val interviewSessionService: InterviewSessionService,
                                 val interviewService: InterviewService,
                                 val notificationService: NotificationService) {


    @PostMapping
    fun createInterviewSession(@RequestAttribute(ClientAccountFilter.CLIENT_KEY) clientAccount: ClientAccount,
                               @Valid @RequestBody request: InterviewSessionRequest): InterviewSessionResponse {

        interviewService.getInterview(request.interviewId).let {
            return interviewSessionService.saveInterviewSession(request.toEntity(it)).toDTO()
        }
    }

    @GetMapping("/{id}")
    fun getInterviewSession(@PathVariable id: String): InterviewSessionResponse {

        return interviewSessionService.getInterviewSession(id).toDTO()
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