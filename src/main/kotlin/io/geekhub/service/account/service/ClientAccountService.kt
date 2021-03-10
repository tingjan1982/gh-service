package io.geekhub.service.account.service

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientUser

interface ClientAccountService {

    fun saveClientAccount(clientAccount: ClientAccount): ClientAccount

    fun getClientAccount(id: String): ClientAccount

    fun getClientOrganizationAccount(id: String): ClientAccount

    fun enableOrganization(clientUser: ClientUser, organizationName: String): ClientAccount

    fun inviteOrganizationUser(clientUser: ClientUser, organizationAccount: ClientAccount, email: String): ClientAccount

    fun uninviteOrganizationUser(clientUser: ClientUser, organizationAccount: ClientAccount, email: String): ClientAccount

    fun joinOrganization(clientUser: ClientUser, organizationAccount: ClientAccount): ClientAccount

    fun leaveOrganization(clientUser: ClientUser)

    fun getInvitedCorporateAccounts(email: String): List<ClientAccount.UserInvitation>
}