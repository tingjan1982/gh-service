package io.geekhub.service.auth0.service

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "auth0.api")
@ConstructorBinding
data class Auth0ApiProperties(val clientId: String,
                              val secret: String,
                              val audience: String)