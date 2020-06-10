package io.geekhub.service.shared.extensions

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt

fun currentClient(): String? {

    val authentication: Authentication = SecurityContextHolder.getContext().authentication

    val principal = authentication.principal
    if (principal is Jwt) {
        return principal.claims["https://api.geekhub.tw/email"] as String
    }

    return null

}