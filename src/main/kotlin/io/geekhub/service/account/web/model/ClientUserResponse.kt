package io.geekhub.service.account.web.model

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientUser

data class ClientUserResponse(
        val id: String,
        val email: String,
        val name: String,
        val nickname: String?,
        val avatar: String?,
        val userType: ClientUser.UserType,
        val accountType: ClientAccount.AccountType,
        val accountPrivilege: ClientUser.AccountPrivilege,
        val organization: OrganizationResponse?,
        val department: ClientDepartmentResponse?,
        val metadata: Map<String, Any>? = mapOf(),
        /**
         * received organization invitations.
         */
        val invitations: List<UserInvitationResponse>
) {
        data class OrganizationResponse(val id: String, val name: String)
}