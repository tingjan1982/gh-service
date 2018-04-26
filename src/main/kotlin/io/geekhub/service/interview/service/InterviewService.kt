package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.Interview

interface InterviewService {

    fun createInterview(interviewOption: InterviewOption): Interview
}