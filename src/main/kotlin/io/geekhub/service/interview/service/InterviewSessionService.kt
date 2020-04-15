package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.shared.model.SearchCriteria
import org.springframework.data.domain.Page

interface InterviewSessionService {

    fun saveInterviewSession(interviewSession: InterviewSession): InterviewSession

    fun sendInterviewSession(interviewSession: InterviewSession): InterviewSession

    fun startInterviewSession(interviewSession: InterviewSession): InterviewSession

    fun addAnswerAttempt(interviewSession: InterviewSession, answerAttempt: InterviewSession.QuestionAnswerAttempt): InterviewSession

    fun submitInterviewSession(interviewSession: InterviewSession): InterviewSession

    fun getInterviewSession(id: String): InterviewSession

    fun getInterviewSessions(searchCriteria: SearchCriteria): Page<InterviewSession>
}