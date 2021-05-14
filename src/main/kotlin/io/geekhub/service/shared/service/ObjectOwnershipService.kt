package io.geekhub.service.shared.service

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.shared.service.data.ClientUserObject

interface ObjectOwnershipService {

    fun <T: ClientUserObject> checkObjectOwnership(clientUser: ClientUser, provider: () -> T): T
}