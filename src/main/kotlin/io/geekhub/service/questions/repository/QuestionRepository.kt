package io.geekhub.service.questions.repository

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.questions.model.Question
import org.springframework.data.repository.PagingAndSortingRepository

interface QuestionRepository : PagingAndSortingRepository<Question, String> {

    /**
     * Put here as an example to query by TextCriteria.
     */
    //fun findAllByClientAccount(clientAccount: ClientAccount, textCriteria: TextCriteria, page: Pageable): Page<Question>

    fun findAllByClientUser(clientUser: ClientUser): List<Question>
}
