package io.geekhub.service.shared.authorization

import io.geekhub.service.user.service.UserService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.TokenEnhancer
import org.springframework.stereotype.Component

/**
 * TokenEnhancer to look up platform user's user id and add as additional information
 * to the token.
 */
@Component
class UserIdTokenEnhancer(val userService: UserService) : TokenEnhancer {

    override fun enhance(accessToken: OAuth2AccessToken, authentication: OAuth2Authentication): OAuth2AccessToken {

        val additionalInfo = mutableMapOf<String, Any>()
        lateinit var userId: String

        when {
            authentication.principal is UserDetails -> {
                val userDetails = authentication.principal as UserDetails

                if (userDetails.username == "admin") {
                    userId = "0"

                } else this.userService.getUserByUsername(userDetails.username).ifPresent {
                    userId = it.userId.toString()
                }
            }

            else -> {
                userId = "UNRESOLVED"
            }
        }


        additionalInfo["userId"] = userId

        (accessToken as DefaultOAuth2AccessToken).additionalInformation = additionalInfo

        return accessToken
    }
}