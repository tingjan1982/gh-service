package io.geekhub.service.interview.web.model

import com.fasterxml.jackson.annotation.JsonIgnore
import io.geekhub.service.account.web.model.ClientAccountResponse
import io.geekhub.service.shared.model.PageableResponse
import io.geekhub.service.shared.model.Visibility
import io.geekhub.service.specialization.web.model.SpecializationResponse
import org.springframework.data.domain.Page

data class InterviewsResponse(@JsonIgnore val page: Page<LightInterviewResponse>,
                              @JsonIgnore val contextPath: String,
                              @JsonIgnore val resourcePrefix: String) : PageableResponse<InterviewsResponse.LightInterviewResponse>(page, contextPath, resourcePrefix) {

    data class LightInterviewResponse(
            val id: String,
            val title: String,
            val description: String?,
            val jobTitle: String,
            val clientAccount: ClientAccountResponse,
            val specialization: SpecializationResponse,
            val visibility: Visibility,
            val published: Boolean
    )
}
