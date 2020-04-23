package io.geekhub.service.interview.repository

import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.model.PublishedInterview
import org.springframework.data.repository.PagingAndSortingRepository

interface InterviewSessionRepository : PagingAndSortingRepository<InterviewSession, String> {

    fun findByPublishedInterviewAndUserEmailAndStatus(publishedInterview: PublishedInterview, userEmail: String, status: InterviewSession.Status): InterviewSession?
}