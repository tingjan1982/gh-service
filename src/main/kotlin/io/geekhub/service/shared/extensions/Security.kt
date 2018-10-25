package io.geekhub.service.shared.extensions

import io.geekhub.service.shared.exception.UnexpectedAuthException
import io.geekhub.service.user.model.User
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails

fun SecurityContext.currentUser(): User {

    val oauth2Authentication = SecurityContextHolder.getContext().authentication as OAuth2Authentication
    val userDetails = oauth2Authentication.details as? OAuth2AuthenticationDetails
            ?: throw UnexpectedAuthException("get current user information.")

    return (userDetails.decodedDetails as User)
}