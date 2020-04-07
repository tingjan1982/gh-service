package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.Interview
import io.geekhub.service.shared.model.SearchCriteria
import org.springframework.data.domain.Page

interface InterviewService {

    fun saveInterview(interview: Interview): Interview

    fun getInterview(id: String): Interview

    fun deleteInterview(id: String)

    fun getInterviews(searchCriteria: SearchCriteria): Page<Interview>
}