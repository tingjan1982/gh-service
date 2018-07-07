package io.geekhub.service.shared.config

import io.geekhub.service.shared.annotation.IntegrationTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

@IntegrationTest
internal class SecurityConfigIntegrationTest {

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Test
    fun authenticateAdminUser() {

        val authResult = this.authenticationManager.authenticate(UsernamePasswordAuthenticationToken("admin", "admin"))
        assertTrue(authResult.isAuthenticated)
    }
}