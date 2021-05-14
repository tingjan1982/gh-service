package io.geekhub.service.interview.repository

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.model.PublishedInterview
import org.springframework.data.repository.PagingAndSortingRepository

interface InterviewSessionRepository : PagingAndSortingRepository<InterviewSession, String> {

    fun existsByPublishedInterviewAndUserEmail(publishedInterview: PublishedInterview, userEmail: String): Boolean

    fun findByPublishedInterviewAndCandidateUser(publishedInterview: PublishedInterview, candidateUser: ClientUser): InterviewSession?
}