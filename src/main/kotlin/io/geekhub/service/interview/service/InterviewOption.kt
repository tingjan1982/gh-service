package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.Interview

data class InterviewOption(val username: String, val interviewMode: Interview.InterviewMode, val duration: Int)