package io.geekhub.service.shared.authorization

import io.geekhub.service.user.model.User
import io.geekhub.service.user.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter
import org.springframework.stereotype.Component

/**
 * Adds additional information to Authentication object's 'details' reference.
 */
@Component
class CustomAccessTokenConverter(val userRepository: UserRepository) : DefaultAccessTokenConverter() {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(CustomAccessTokenConverter::class.java)
    }

    override fun extractAuthentication(map: MutableMap<String, *>): OAuth2Authentication {
        val authentication = super.extractAuthentication(map)

        map["userId"]?.let { it ->
            val userId = it as String
            logger.debug("Attempt to get user with id $userId")

            authentication.details = when (userId) {
                "0" -> {
                    User("0", "admin", "Admin", "Admin", "admin@geekhub.tw")
                }

                else -> this.userRepository.getOne(userId)
            }
        }

        logger.debug("Resolved user: ${authentication.details}")

        return authentication
    }
}