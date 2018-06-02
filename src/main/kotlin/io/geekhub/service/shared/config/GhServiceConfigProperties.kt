package io.geekhub.service.shared.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource(value = ["classpath:gh-service.properties"])
@ConfigurationProperties(prefix = "ghservice")
class GhServiceConfigProperties {

    var csrfEnabled: Boolean = false

}