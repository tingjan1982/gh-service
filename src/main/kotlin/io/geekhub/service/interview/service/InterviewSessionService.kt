package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.InterviewSession

interface InterviewSessionService {

    fun saveInterviewSession(interviewSession: InterviewSession): InterviewSession

    fun getInterviewSession(id: String): InterviewSession
}