package io.geekhub.service.shared.exception

import org.springframework.security.core.AuthenticationException

class UnexpectedAuthException(msg: String) : AuthenticationException("$errorMsgPrefix $msg") {

    companion object {
        const val errorMsgPrefix = "Error during: "
    }
}