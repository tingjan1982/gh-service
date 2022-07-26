package io.geekhub.service.interview.service

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.model.PublishedInterview
import io.geekhub.service.shared.model.SearchCriteria
import org.springframework.data.domain.Page

interface InterviewService {

    fun saveInterview(interview: Interview): Interview

    fun saveInterview(interview: Interview, validate: Boolean): Interview

    fun saveInterviewDirectly(interview: Interview): Interview

    fun copyInterview(interview: Interview, clientUser: ClientUser): Interview

    fun getInterview(id: String): Interview

    fun updateInterviewOwner(interview: Interview, userId: String): Interview

    fun getPublishedInterviewByInterview(interviewId: String) : PublishedInterview

    fun getPublishedInterviewByPublishedId(publishedInterviewId: String?) : PublishedInterview

    fun deleteInterview(id: String)

    fun getInterviews(searchCriteria: SearchCriteria): Page<Interview>

    fun getInterviews(clientUser: ClientUser): List<Interview>
    fun getInterviewsCount(clientUser: ClientUser): Int
}