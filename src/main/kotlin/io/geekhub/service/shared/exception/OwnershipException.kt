package io.geekhub.service.shared.exception

import io.geekhub.service.account.repository.ClientAccount

class OwnershipException(errorMsg: String) : RuntimeException(errorMsg) {

    companion object {
        fun notSameOrganization(owningAccount: ClientAccount): OwnershipException {
            return OwnershipException("User is not part of organization ${owningAccount.clientName}")
        }

        fun notOwner(): OwnershipException {
            return OwnershipException("Only owner or administrator can perform this action")
        }
    }
}