package io.geekhub.service.account.repository

/**
 * Available fields: https://auth0.com/docs/api/management/v2/#!/Users/get_users_by_id
 */
data class Auth0UserInfo(
        val sub: String,
        val name: String,
        val nickname: String,
        val email: String,
        val locale: List<String>?,
        val picture: String
) {

    fun getUserType(): ClientUser.UserType {

        return if (sub.startsWith("github"))
            ClientUser.UserType.GITHUB
        else
            ClientUser.UserType.AUTH0
    }
}