package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.InterviewSession

interface InterviewSessionService {

    fun saveInterviewSession(interviewSession: InterviewSession): InterviewSession

    fun sendInterviewSession(interviewSession: InterviewSession): InterviewSession

    fun startInterviewSession(interviewSession: InterviewSession): InterviewSession

    fun addAnswerAttempt(interviewSession: InterviewSession, questionId: String, answerAttempt: InterviewSession.QuestionAnswerAttempt): InterviewSession

    fun submitInterviewSession(interviewSession: InterviewSession): InterviewSession

    fun getInterviewSession(id: String): InterviewSession
}