package io.geekhub.service.interview.web

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.service.InterviewService
import io.geekhub.service.interview.service.InterviewSessionService
import io.geekhub.service.interview.toDTO
import io.geekhub.service.interview.toEntity
import io.geekhub.service.interview.web.model.InterviewSessionRequest
import io.geekhub.service.interview.web.model.InterviewSessionResponse
import io.geekhub.service.notification.service.NotificationService
import io.geekhub.service.shared.web.filter.ClientAccountFilter
import org.springframework.web.bind.annotation.*
import java.util.*
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
    fun startInterviewSession(@RequestAttribute(ClientAccountFilter.CLIENT_KEY) clientAccount: ClientAccount,
                              @PathVariable id: String): InterviewSessionResponse {

        interviewSessionService.getInterviewSession(id).let {
            it.interviewSentDate = Date()
            notificationService.sendNotification(it)

            return interviewSessionService.saveInterviewSession(it).toDTO()
        }
    }
}