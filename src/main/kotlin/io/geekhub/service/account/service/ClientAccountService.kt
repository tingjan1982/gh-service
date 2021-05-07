package io.geekhub.service.account.service

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientUser

interface ClientAccountService {

    fun saveClientAccount(clientAccount: ClientAccount): ClientAccount

    fun getClientAccount(id: String): ClientAccount

    fun getClientOrganizationAccount(id: String): ClientAccount

    fun enableOrganization(clientUser: ClientUser, organizationName: String): ClientAccount

    fun inviteOrganizationUser(inviter: ClientUser, organizationAccount: ClientAccount, inviteeEmail: String): ClientAccount

    fun uninviteOrganizationUser(clientUser: ClientUser, organizationAccount: ClientAccount, email: String): ClientAccount

    fun joinOrganization(clientUser: ClientUser, organizationAccount: ClientAccount): ClientAccount

    fun userDeclineOrganizationInvitation(clientUser: ClientUser, organizationAccount: ClientAccount)

    fun leaveOrganization(clientUser: ClientUser)

    fun getInvitedCorporateAccounts(email: String): List<ClientAccount.UserInvitation>
}