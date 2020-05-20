package io.geekhub.service.interview.web

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.service.InterviewService
import io.geekhub.service.interview.toDTO
import io.geekhub.service.interview.web.model.PublishedInterviewResponse
import io.geekhub.service.shared.web.filter.ClientAccountFilter
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/publishedInterviews")
class PublishedInterviewController(val interviewService: InterviewService) {

    @GetMapping("/{id}")
    fun getPublishedInterview(@RequestAttribute(ClientAccountFilter.CLIENT_KEY) clientAccount: ClientAccount,
                              @PathVariable id: String): PublishedInterviewResponse {

        return interviewService.getPublishedInterviewByPublishedId(id).toDTO(clientAccount)
    }
}