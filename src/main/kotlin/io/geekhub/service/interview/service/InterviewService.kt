package io.geekhub.service.interview.service

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.model.PublishedInterview
import io.geekhub.service.shared.model.SearchCriteria
import org.springframework.data.domain.Page

interface InterviewService {

    fun saveInterview(interview: Interview): Interview

    fun getInterview(id: String): Interview

    fun publishInterview(id: String): PublishedInterview

    fun updateInterviewOwner(interview: Interview, userId: String): Interview

    fun getPublishedInterviewByInterview(interviewId: String) : PublishedInterview

    fun getPublishedInterviewByPublishedId(publishedInterviewId: String?) : PublishedInterview

    fun deleteInterview(id: String)

    fun getInterviews(searchCriteria: SearchCriteria): Page<Interview>

    fun getInterviews(clientUser: ClientUser): List<Interview>
}