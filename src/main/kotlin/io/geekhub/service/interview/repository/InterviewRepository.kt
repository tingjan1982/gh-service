package io.geekhub.service.interview.repository

import io.geekhub.service.interview.model.Interview
import org.springframework.data.repository.PagingAndSortingRepository

interface InterviewRepository : PagingAndSortingRepository<Interview, String>