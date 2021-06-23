package io.geekhub.service.account.repository

import io.geekhub.service.binarystorage.data.BinaryFile
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Represents an user in the system. It contains user profile related information such as
 * email, nickname, avatar and so on.
 */
@Document
data class ClientUser(
    @Id
    var id: String? = null,
    var email: String,
    var name: String,
    var nickname: String?,
    var avatar: String? = null,
    val userType: UserType,
    var accountPrivilege: AccountPrivilege = AccountPrivilege.OWNER,
    @DBRef
    var clientAccount: ClientAccount,
    @DBRef
    var department: ClientDepartment? = null,
    @DBRef
    var avatarBinary: BinaryFile? = null) {

    fun isOrgOwner(): Boolean {
        return isCorporateAccount() && accountPrivilege == AccountPrivilege.OWNER
    }

    fun individualAccount(): Boolean {
        return clientAccount.accountType == ClientAccount.AccountType.INDIVIDUAL
    }

    fun isCorporateAccount(): Boolean {
        return clientAccount.accountType == ClientAccount.AccountType.CORPORATE
    }

    /**
     * UserType will allow the system to determine if user profile information can be updated and synced
     * back to Auth0.
     */
    enum class UserType {
        AUTH0, GITHUB
    }

    enum class AccountPrivilege {

        /**
         * Organization owner.
         */
        OWNER,

        /**
         * Administrator.
         */
        ADMIN,

        /**
         * Standard user.
         */
        USER
    }
}