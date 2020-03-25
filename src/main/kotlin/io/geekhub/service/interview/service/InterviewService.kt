package io.geekhub.service.interview.service

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.model.Interview
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

interface InterviewService {

    fun saveInterview(interview: Interview): Interview

    fun getInterview(id: String): Interview

    fun deleteInterview(id: String)

    fun getInterviews(clientAccount: ClientAccount, pageRequest: PageRequest): Page<Interview>
}