package io.geekhub.service.account.web.model

import io.geekhub.service.account.repository.ClientAccount

data class ClientAccountResponse(
        val id: String,
        val clientName: String,
        val accountType: ClientAccount.AccountType,
        val planType: ClientAccount.PlanType
)

