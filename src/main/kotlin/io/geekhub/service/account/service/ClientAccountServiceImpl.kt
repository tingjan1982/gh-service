package io.geekhub.service.account.service

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientAccountRepository
import org.springframework.stereotype.Service

@Service
class ClientAccountServiceImpl(val repository: ClientAccountRepository) : ClientAccountService {

    override fun saveClientAccount(clientAccount: ClientAccount): ClientAccount {
        return repository.save(clientAccount)
    }

    override fun getClientAccount(id: String): ClientAccount? {
        return repository.findById(id).orElse(null)
    }
}