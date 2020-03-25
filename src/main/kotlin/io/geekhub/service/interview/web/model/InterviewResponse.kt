package io.geekhub.service.interview.web.model

import io.geekhub.service.account.web.model.ClientAccountResponse
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.specialization.repository.Specialization

data class InterviewResponse(
        val id: String,
        val title: String,
        val description: String?,
        val jobTitle: String,
        val clientAccount: ClientAccountResponse,
        val specialization: Specialization,
        val sections: List<SectionResponse>
) {

    data class SectionResponse(
            val title: String,
            val questions: List<Interview.QuestionSnapshot>
    )
}
