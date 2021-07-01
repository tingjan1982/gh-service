package io.geekhub.service.interview.web.model

import com.fasterxml.jackson.annotation.JsonIgnore
import io.geekhub.service.account.web.model.LightClientUserResponse
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.shared.extensions.toLightDTO
import io.geekhub.service.shared.model.PageableResponse
import io.geekhub.service.shared.model.Visibility
import org.springframework.data.domain.Page
import org.springframework.web.util.UriComponentsBuilder
import java.util.*

data class InterviewsResponse(@JsonIgnore val page: Page<LightInterviewResponse>,
                              @JsonIgnore val navigationLinkBuilder: UriComponentsBuilder) :
    PageableResponse<InterviewsResponse.LightInterviewResponse>(page, navigationLinkBuilder) {

    data class LightInterviewResponse(
        val id: String,
        val title: String,
        val description: String?,
        val jobTitle: String,
        val clientUser: LightClientUserResponse,
        val visibility: Visibility,
        val defaultDuration: Int,
        val publishedInterviewId: String?,
        val likeCount: Long,
        val liked: Boolean,
        val createdDate: Date?,
        val lastModifiedDate: Date?,
        val groupedInterviewSessions: Map<InterviewSession.Status, List<String>>
    )
}

fun Interview.toLightDTO(likedByClientUser: Boolean = false) = InterviewsResponse.LightInterviewResponse(
    id = this.id.toString(),
    title = this.title,
    description = this.description,
    jobTitle = this.jobTitle,
    clientUser = this.clientUser.toLightDTO(),
    visibility = this.visibility,
    defaultDuration = this.defaultDuration,
    publishedInterviewId = this.latestPublishedInterviewId,
    likeCount = this.likeCount,
    liked = likedByClientUser,
    createdDate = this.createdDate,
    lastModifiedDate = this.lastModifiedDate,
    groupedInterviewSessions = this.groupInterviewSessions()
)
