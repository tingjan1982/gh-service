package io.geekhub.service.interview.web.model

import com.fasterxml.jackson.annotation.JsonIgnore
import io.geekhub.service.account.web.model.ClientAccountResponse
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.shared.model.PageableResponse
import io.geekhub.service.shared.model.Visibility
import io.geekhub.service.specialization.web.model.SpecializationResponse
import org.springframework.data.domain.Page
import org.springframework.web.util.UriComponentsBuilder

data class InterviewsResponse(@JsonIgnore val page: Page<LightInterviewResponse>,
                              @JsonIgnore val navigationLinkBuilder: UriComponentsBuilder) : PageableResponse<InterviewsResponse.LightInterviewResponse>(page, navigationLinkBuilder) {

    data class LightInterviewResponse(
            val id: String,
            val title: String,
            val description: String?,
            val jobTitle: String,
            val clientAccount: ClientAccountResponse,
            val specialization: SpecializationResponse,
            val visibility: Visibility,
            val defaultDuration: Int,
            val publishedInterviewId: String?,
            val likeCount: Long,
            val liked: Boolean,
            val interviewSessions: Map<InterviewSession.Status, List<String>>
    )
}
