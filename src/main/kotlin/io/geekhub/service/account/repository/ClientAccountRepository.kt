package io.geekhub.service.account.repository

import org.springframework.data.repository.CrudRepository

interface ClientAccountRepository : CrudRepository<ClientAccount, String> {

    fun findByIdAndAccountType(id: String, accountType: ClientAccount.AccountType): ClientAccount?
}