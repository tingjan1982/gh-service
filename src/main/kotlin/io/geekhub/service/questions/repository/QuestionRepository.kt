package io.geekhub.service.questions.repository

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.questions.model.Question
import io.geekhub.service.specialization.repository.Specialization
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.query.TextCriteria
import org.springframework.data.repository.PagingAndSortingRepository

interface QuestionRepository : PagingAndSortingRepository<Question, String> {

    /**
     * Put here as an example to query by TextCriteria.
     */
    fun findAllByClientAccount(clientAccount: ClientAccount, textCriteria: TextCriteria, page: Pageable): Page<Question>

    fun countBySpecialization(specialization: Specialization): Long
}
