package io.geekhub.service.account.service

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientUser

interface ClientAccountService {

    fun saveClientAccount(clientAccount: ClientAccount): ClientAccount

    fun getClientAccount(id: String): ClientAccount

    fun lookupClientAccount(id: String): ClientAccount?

    fun getClientOrganizationAccount(id: String): ClientAccount

    fun enableOrganization(clientUser: ClientUser, organizationName: String): ClientAccount

    fun changeOrganizationOwner(currentOwner: ClientUser, newOwner: ClientUser)

    fun inviteOrganizationUser(inviter: ClientUser, organizationAccount: ClientAccount, inviteeEmail: String): ClientAccount

    fun uninviteOrganizationUser(clientUser: ClientUser, organizationAccount: ClientAccount, email: String): ClientAccount

    fun joinOrganization(clientUser: ClientUser, organizationAccount: ClientAccount): ClientAccount

    fun userDeclineOrganizationInvitation(clientUser: ClientUser, organizationAccount: ClientAccount)

    fun removeUserFromOrganization(clientUser: ClientUser, userToRemove: ClientUser)

    fun leaveOrganization(clientUser: ClientUser, organizationAccount: ClientAccount)

    fun getInvitedCorporateAccounts(email: String): List<ClientAccount.UserInvitation>
}