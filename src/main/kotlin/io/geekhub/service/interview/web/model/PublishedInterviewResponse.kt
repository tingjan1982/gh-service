package io.geekhub.service.interview.web.model

data class PublishedInterviewResponse(
        val publishedId: String,
        val referencedInterview: InterviewResponse
)
