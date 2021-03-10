package io.geekhub.service.account.service

import io.geekhub.service.account.repository.Auth0UserInfo
import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientUser

interface ClientUserService {

    fun saveClientUser(clientUser: ClientUser): ClientUser

    fun lookupClientUser(id: String): ClientUser?

    fun getClientUser(id: String): ClientUser

    fun getClientAccountOwner(clientAccount: ClientAccount): ClientUser

    fun getAuth0UserInfo(token: String): Auth0UserInfo
}