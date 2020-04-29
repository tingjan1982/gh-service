package io.geekhub.service.interview.repository

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.model.PublishedInterview
import org.springframework.data.repository.PagingAndSortingRepository

interface InterviewSessionRepository : PagingAndSortingRepository<InterviewSession, String> {

    fun findByPublishedInterviewAndCandidateAccountAndStatusIn(publishedInterview: PublishedInterview, candidateAccount: ClientAccount, status: List<InterviewSession.Status>): InterviewSession?
}