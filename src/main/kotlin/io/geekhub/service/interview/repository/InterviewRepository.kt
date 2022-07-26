package io.geekhub.service.interview.repository

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.interview.model.Interview
import org.springframework.data.repository.PagingAndSortingRepository

interface InterviewRepository : PagingAndSortingRepository<Interview, String> {

    fun findAllByClientUser(clientUser: ClientUser): List<Interview>

    fun countAllByClientUser(clientUser: ClientUser): Int
}