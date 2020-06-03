package io.geekhub.service.auth0.service.bean

data class Auth0User(val email: String,
                     val password: String,
                     val connection: String = "Username-Password-Authentication")
