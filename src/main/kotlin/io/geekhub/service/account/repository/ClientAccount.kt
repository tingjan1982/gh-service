package io.geekhub.service.account.repository

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Represents an account within the system. Account differentiates from User in the sense that
 * it tracks account related information such as account type, which can used for reporting,
 * plan type, which indicates the plan that the user is on.
 */
@Document
data class ClientAccount(
        @Id
        var id: String? = null,
        var accountType: AccountType,
        var planType: PlanType, // todo: move plan to another class
        var clientName: String, // e.g. corporate name
        val userInvitations: MutableSet<UserInvitation> = mutableSetOf()
) {
    fun addUserInvitation(userInvitation: UserInvitation): ClientAccount {
        userInvitations.add(userInvitation)
        return this
    }

    fun removeUserInvitation(email: String): ClientAccount {
        userInvitations.removeIf {
            it.email == email && it.status != InvitationStatus.JOINED
        }

        return this
    }

    fun userInvitationJoined(email: String) {
        userInvitations.forEach {
            if (it.email == email) {
                it.status = InvitationStatus.JOINED
            }
        }
    }

    enum class AccountType {
        CORPORATE, INDIVIDUAL
    }

    enum class PlanType {
        FREE, PREMIUM
    }

    data class UserInvitation(
        val email: String,
        var status: InvitationStatus = InvitationStatus.INVITED
    )

    enum class InvitationStatus {
        INVITED, JOINED
    }
}