package io.geekhub.service.account.repository

import io.geekhub.service.binarystorage.data.BinaryFile
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Represents an account within the system. Account differentiates from User in the sense that
 * it tracks account related information such as account type, which can used for reporting,
 * plan type, which indicates the plan that the user is on.
 *
 * Data class behavior:
 * https://kotlinlang.org/docs/data-classes.html
 */
@Document
data class ClientAccount(
    @Id var id: String? = null,
    var accountType: AccountType,
    var planType: PlanType, // todo: move plan to another class
    var clientName: String
) {

    @DBRef
    var avatarBinary: BinaryFile? = null

    @DBRef(lazy = true)
    var users: MutableSet<ClientUser> = mutableSetOf()

    var userInvitations: MutableSet<UserInvitation> = mutableSetOf()


    fun addUser(clientUser: ClientUser) {
        users.add(clientUser)
    }

    fun removeUser(clientUser: ClientUser) {
        users.remove(clientUser)
    }

    fun addUserInvitation(inviter: ClientUser, email: String): UserInvitation {

        getUserInvitation(email)?.let {
            return it

        } ?: UserInvitation(
            inviter.id.toString(),
            inviter.name,
            inviter.email,
            inviter.clientAccount.clientName,
            inviter.clientAccount.id,
            email,
            InvitationStatus.INVITED
        ).let {
            userInvitations.add(it)

            return it
        }
    }

    fun userInvitationJoined(clientUser: ClientUser) {
        addUser(clientUser)
        removeUserInvitation(clientUser.email)
    }

    fun removeUserInvitation(email: String): ClientAccount {
        userInvitations.removeIf {
            it.email == email
        }

        return this
    }

    fun userInvitationDeclined(email: String) {
        getUserInvitation(email)?.let {
            it.status = InvitationStatus.DECLINED
        }
    }

    private fun getUserInvitation(email: String): UserInvitation? {
        return userInvitations.find { it.email == email }
    }

    fun clearUserInvitations() {
        userInvitations.clear()
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
        var inviterOrganizationId: String?,
        val email: String,
        var status: InvitationStatus = InvitationStatus.INVITED
    )

    enum class InvitationStatus {
        INVITED, DECLINED
    }
}