package io.geekhub.service.account.repository

import io.geekhub.service.binarystorage.data.BinaryFile
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Represents an account within the system. Account differentiates from User in the sense that
 * it tracks account related information such as account type, which can used for reporting,
 * plan type, which indicates the plan that the user is on.
 */
@Document
data class ClientAccount(
    @Id var id: String? = null,
    var accountType: AccountType,
    var planType: PlanType, // todo: move plan to another class
    var clientName: String, // e.g. organization name
    @DBRef
    var avatarBinary: BinaryFile? = null,
    val userInvitations: MutableSet<UserInvitation> = mutableSetOf()) {

    fun addUserInvitation(inviter: ClientUser, email: String): UserInvitation {

        UserInvitation(inviter.id.toString(), inviter.name, inviter.email, inviter.clientAccount.clientName, email).let {
            userInvitations.add(it)

            return it
        }
    }

    fun getUserInvitation(email: String): UserInvitation? {
        return userInvitations.find { it.email == email }
    }

    fun removeUserInvitation(email: String): ClientAccount {
        userInvitations.removeIf {
            it.email == email
        }

        return this
    }

    fun userInvitationJoined(email: String) {

        removeUserInvitation(email)
    }

    enum class AccountType {
        CORPORATE, INDIVIDUAL
    }

    enum class PlanType {
        FREE, PREMIUM
    }

    data class UserInvitation(
        val inviterId: String,
        val inviterName: String,
        val inviterEmail: String,
        val inviterOrganization: String,
        val email: String
    )
}