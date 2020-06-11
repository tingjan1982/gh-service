package io.geekhub.service.account.repository

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
        var nickname: String,
        var avatar: String? = null,
        val userType: UserType,
        @DBRef
        var clientAccount: ClientAccount
) {

    /**
     * UserType will allow the system to determine if user profile information can be updated and synced
     * back to Auth0.
     */
    enum class UserType {
        AUTH0, GITHUB
    }
}