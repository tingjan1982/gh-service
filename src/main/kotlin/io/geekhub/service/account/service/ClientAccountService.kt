package io.geekhub.service.account.service

import io.geekhub.service.account.repository.ClientAccount

interface ClientAccountService {

    fun saveClientAccount(clientAccount: ClientAccount): ClientAccount

    fun getClientAccount(id: String): ClientAccount?

}