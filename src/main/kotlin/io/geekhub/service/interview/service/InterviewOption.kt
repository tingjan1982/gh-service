package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.Interview

data class InterviewOption(val user: String, val interviewMode: Interview.InterviewMode, val duration: Int)