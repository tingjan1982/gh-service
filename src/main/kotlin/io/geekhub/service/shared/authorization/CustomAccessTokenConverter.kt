package io.geekhub.service.shared.authorization

import io.geekhub.service.user.model.User
import io.geekhub.service.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter
import org.springframework.stereotype.Component

/**
 * Adds additional information to Authentication object's 'details' reference.
 */
@Component
class CustomAccessTokenConverter(val userService: UserService) : DefaultAccessTokenConverter() {

    companion object {
        val logger = LoggerFactory.getLogger(CustomAccessTokenConverter::class.java)
    }

    override fun extractAuthentication(map: MutableMap<String, *>): OAuth2Authentication {
        val authentication = super.extractAuthentication(map)

        map["userId"]?.let { it ->
            val userId = it as String
            logger.info("Attempt to get user")

            when (userId) {
                "0" -> {
                    authentication.details = User("0", "admin", "Admin", "Admin", "admin@geekhub.tw")
                }
                "UNRESOLVED" -> {
                    logger.warn("User ID was not resolvable from the access token: $authentication")
                }
                else -> {
                    this.userService.getUser(userId).let {
                        authentication.details = it
                    }
                }
            }
        }

        logger.debug("Resolved user: ${authentication.details}")

        return authentication
    }
}