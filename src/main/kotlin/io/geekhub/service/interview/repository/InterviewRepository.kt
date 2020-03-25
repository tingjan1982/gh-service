package io.geekhub.service.interview.repository

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.model.Interview
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.PagingAndSortingRepository

interface InterviewRepository : PagingAndSortingRepository<Interview, String> {

    fun findAllByClientAccount(clientAccount: ClientAccount, pageRequest: PageRequest): Page<Interview>
}