package io.geekhub.service.auth0.service

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

/**
 * https://towardsdatascience.com/a-guide-to-use-spring-boots-configurationproperties-annotation-in-kotlin-s-dataclass-1341c63110f4
 */
@ConfigurationProperties(prefix = "auth0.management")
@ConstructorBinding
data class Auth0ManagementApiProperties(val clientId: String,
                                        val secret: String,
                                        val audience: String)