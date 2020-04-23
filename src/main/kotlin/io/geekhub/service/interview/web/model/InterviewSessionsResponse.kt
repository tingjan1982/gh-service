package io.geekhub.service.interview.web.model

import com.fasterxml.jackson.annotation.JsonIgnore
import io.geekhub.service.account.web.model.ClientAccountResponse
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.shared.model.PageableResponse
import org.springframework.data.domain.Page
import org.springframework.web.util.UriComponentsBuilder
import java.math.BigDecimal
import java.util.*

data class InterviewSessionsResponse(@JsonIgnore val page: Page<LightInterviewSessionResponse>,
                                     @JsonIgnore val navigationLinkBuilder: UriComponentsBuilder) : PageableResponse<InterviewSessionsResponse.LightInterviewSessionResponse>(page, navigationLinkBuilder) {

    data class LightInterviewSessionResponse(
            val id: String,
            val publishedInterviewId: String,
            val interview: InterviewsResponse.LightInterviewResponse,
            val clientAccount: ClientAccountResponse,
            val userEmail: String,
            val name: String?,
            val interviewMode: InterviewSession.InterviewMode,
            val duration: Int,
            val status: InterviewSession.Status,
            val interviewSentDate: Date?,
            val interviewStartDate: Date?,
            val interviewEndDate: Date?,
            val score: BigDecimal)

}
