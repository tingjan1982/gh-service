package io.geekhub.service.interview.repository

import io.geekhub.service.interview.model.InterviewSession
import org.springframework.data.repository.PagingAndSortingRepository

interface InterviewSessionRepository : PagingAndSortingRepository<InterviewSession, String> {
}