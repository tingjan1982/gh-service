package io.geekhub.service.interview.web.model

data class UpdateInterviewSessionRequest(
        val userEmail: String,
        val name: String?
)
