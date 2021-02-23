package io.geekhub.service.account.service

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientUser

interface ClientAccountService {

    fun saveClientAccount(clientAccount: ClientAccount): ClientAccount

    fun getClientAccount(id: String): ClientAccount?

    fun enableOrganization(clientUser: ClientUser, organizationName: String): ClientAccount

    fun inviteOrganizationUser(clientUser: ClientUser, email: String): ClientAccount

    fun uninviteOrganizationUser(clientUser: ClientUser, email: String): ClientAccount
}