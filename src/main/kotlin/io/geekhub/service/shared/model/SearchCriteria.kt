package io.geekhub.service.shared.model

import io.geekhub.service.account.repository.ClientAccount
import org.springframework.data.domain.PageRequest

data class SearchCriteria(
        val filterByClientAccount: Boolean,
        val clientAccount: ClientAccount,
        val keyword: String?,
        val pageRequest: PageRequest
)