package io.geekhub.service.shared.service

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.service.data.ClientUserObject
import org.springframework.stereotype.Service

@Service
class ObjectOwnershipServiceImpl : ObjectOwnershipService {

    override fun <T : ClientUserObject> checkObjectOwnership(clientUser: ClientUser, provider: () -> T): T {
        provider().let {
            if (it.clientUser != clientUser) {
                throw BusinessException("You do not own this client user object: ${it::class.simpleName}")
            }

            return it
        }
    }
}