package io.geekhub.service.interview.repository

import io.geekhub.service.interview.model.Interview
import io.geekhub.service.specialization.repository.Specialization
import org.springframework.data.repository.PagingAndSortingRepository

interface InterviewRepository : PagingAndSortingRepository<Interview, String> {

    fun countBySpecialization(specialization: Specialization): Long
}