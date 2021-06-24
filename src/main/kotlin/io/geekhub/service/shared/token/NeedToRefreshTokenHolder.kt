package io.geekhub.service.shared.token

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

object NeedToRefreshTokenHolder {

    private val LOGGER: Logger = LoggerFactory.getLogger(NeedToRefreshTokenHolder::class.java)

    private val tokens = mutableListOf<String>()

    fun addSecurityContextToken() {

        SecurityContextHolder.getContext().authentication.let {
            if (it is JwtAuthenticationToken) {
                LOGGER.info("Adding token to holder: ${it.token.tokenValue}")

                tokens.add(it.token.tokenValue)
            }
        }
    }

    fun containsNeedToRefreshToken(token: String): Boolean {
        return tokens.contains(token)
    }
}