package io.geekhub.service.account.service

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientAccountRepository
import io.geekhub.service.auth0.service.Auth0ManagementService
import org.springframework.stereotype.Service

@Service
class ClientAccountServiceImpl(val repository: ClientAccountRepository, val auth0ManagementService: Auth0ManagementService) : ClientAccountService {

    override fun saveClientAccount(clientAccount: ClientAccount): ClientAccount {
        return repository.save(clientAccount)
    }

    override fun getClientAccount(id: String): ClientAccount? {
        return repository.findById(id).orElse(null)
    }

    override fun updatePassword(clientAccount: ClientAccount, updatedPassword: String) {

        auth0ManagementService.getManagementToken().let {
            auth0ManagementService.updateUserPassword(clientAccount.id.toString(), updatedPassword, it)
        }
    }
}