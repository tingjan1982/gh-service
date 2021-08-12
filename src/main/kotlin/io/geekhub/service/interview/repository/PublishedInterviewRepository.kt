package io.geekhub.service.interview.repository

import io.geekhub.service.interview.model.PublishedInterview
import org.springframework.data.repository.PagingAndSortingRepository

interface PublishedInterviewRepository : PagingAndSortingRepository<PublishedInterview, String> {

    fun deleteAllByReferencedInterview_Id(interviewId: String)
}