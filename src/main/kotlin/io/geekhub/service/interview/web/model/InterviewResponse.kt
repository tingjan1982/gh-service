package io.geekhub.service.interview.web.model

import io.geekhub.service.account.web.model.ClientAccountResponse
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.specialization.web.model.SpecializationResponse

data class InterviewResponse(
        val id: String,
        val title: String,
        val description: String?,
        val jobTitle: String,
        val clientAccount: ClientAccountResponse,
        val specialization: SpecializationResponse,
        val sections: List<SectionResponse>
) {

    data class SectionResponse(
            val title: String,
            val questions: List<Interview.QuestionSnapshot>
    )
}
