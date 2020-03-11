package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.InterviewSession

data class InterviewOption(val username: String,
                           var interviewMode: InterviewSession.InterviewMode = InterviewSession.InterviewMode.MOCK,
                           var duration: Int = -1,
                           var questionCount: Int = 30)