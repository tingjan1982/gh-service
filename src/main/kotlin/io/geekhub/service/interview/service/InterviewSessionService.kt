package io.geekhub.service.interview.service

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.shared.model.SearchCriteria
import org.springframework.data.domain.Page

interface InterviewSessionService {

    fun createInterviewSession(interviewSession: InterviewSession): InterviewSession

    fun createInterviewSession(interview: Interview): InterviewSession

    fun saveInterviewSession(interviewSession: InterviewSession): InterviewSession

    fun sendInterviewSession(sender: ClientUser, interviewSession: InterviewSession): InterviewSession

    fun startInterviewSession(interviewSession: InterviewSession, candidateUser: ClientUser): InterviewSession

    fun addAnswerAttempt(interviewSession: InterviewSession, answerAttempt: InterviewSession.QuestionAnswerAttempt): InterviewSession

    fun submitInterviewSession(interviewSession: InterviewSession): InterviewSession

    fun markInterviewSessionAnswer(interviewSession: InterviewSession, sectionId: String, questionSnapshotId: String, correct: Boolean): InterviewSession

    fun getInterviewSession(id: String): InterviewSession

    fun getCurrentInterviewSession(interviewId: String, clientUser: ClientUser): InterviewSession

    fun getInterviewSessions(searchCriteria: SearchCriteria, status: InterviewSession.Status?): Page<InterviewSession>

    fun calculateScore(interviewSession: InterviewSession): InterviewSession
}