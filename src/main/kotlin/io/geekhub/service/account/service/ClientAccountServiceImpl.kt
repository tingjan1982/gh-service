package io.geekhub.service.account.service

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientAccountRepository
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.auth0.service.Auth0ManagementService
import io.geekhub.service.shared.exception.BusinessException
import org.springframework.stereotype.Service

@Service
class ClientAccountServiceImpl(val repository: ClientAccountRepository, val auth0ManagementService: Auth0ManagementService) : ClientAccountService {

    override fun saveClientAccount(clientAccount: ClientAccount): ClientAccount {
        return repository.save(clientAccount)
    }

    override fun getClientAccount(id: String): ClientAccount? {
        return repository.findById(id).orElse(null)
    }

    override fun enableOrganization(clientUser: ClientUser, organizationName: String): ClientAccount {

        checkClientUserAccess(clientUser)

        clientUser.clientAccount.apply {
            this.accountType = ClientAccount.AccountType.CORPORATE
            this.clientName = organizationName
        }.let {
            return repository.save(it)
        }
    }

    override fun inviteOrganizationUser(clientUser: ClientUser, email: String): ClientAccount {

        checkClientUserAccess(clientUser)

        if (clientUser.clientAccount.accountType != ClientAccount.AccountType.CORPORATE) {
            throw BusinessException("Please enable organization first")
        }

        clientUser.clientAccount.addUserInvitation(ClientAccount.UserInvitation(email)).let {
            return saveClientAccount(it)
        }
    }

    override fun uninviteOrganizationUser(clientUser: ClientUser, email: String): ClientAccount {

        checkClientUserAccess(clientUser)

        clientUser.clientAccount.removeUserInvitation(email).let {
            return saveClientAccount(it)
        }
    }

    private fun checkClientUserAccess(clientUser: ClientUser) {

        if (!clientUser.hasOrganizationAccess()) {
            throw BusinessException("Only account owner can operate on organization related changes.")
        }
    }
}