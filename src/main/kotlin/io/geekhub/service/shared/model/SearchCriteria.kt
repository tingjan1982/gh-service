package io.geekhub.service.shared.model

import io.geekhub.service.account.repository.ClientAccount

data class SearchCriteria(
        val filterByClientAccount: Boolean = true,
        val clientAccount: ClientAccount,
        val keyword: String?
)