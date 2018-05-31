package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.Interview

data class InterviewOption(val username: String,
                           var interviewMode: Interview.InterviewMode = Interview.InterviewMode.MOCK,
                           var duration: Int = -1,
                           var questionCount: Int = 30)