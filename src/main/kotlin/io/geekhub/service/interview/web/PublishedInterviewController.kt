package io.geekhub.service.interview.web

import io.geekhub.service.interview.service.InterviewService
import io.geekhub.service.interview.toDTO
import io.geekhub.service.interview.web.model.PublishedInterviewResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/publishedInterview")
class PublishedInterviewController(val interviewService: InterviewService) {

    @RequestMapping("/{id}")
    fun getPublishedInterview(@PathVariable id: String): PublishedInterviewResponse {

        return interviewService.getPublishedInterviewByPublishedId(id).toDTO()
    }
}