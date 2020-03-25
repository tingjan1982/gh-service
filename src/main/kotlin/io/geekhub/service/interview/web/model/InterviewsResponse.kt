package io.geekhub.service.interview.web.model

import io.geekhub.service.account.web.model.ClientAccountResponse
import io.geekhub.service.specialization.repository.Specialization

data class InterviewsResponse(
        val results: List<LightInterviewResponse> = mutableListOf()
) {

    data class LightInterviewResponse(
            val id: String,
            val title: String,
            val description: String?,
            val jobTitle: String,
            val clientAccount: ClientAccountResponse,
            val specialization: Specialization
    )
}
