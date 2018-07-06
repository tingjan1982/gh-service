package io.geekhub.service.shared.config

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
internal class SecurityConfigIntegrationTest {

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Test
    fun authenticateAdminUser() {

        val authResult = this.authenticationManager.authenticate(UsernamePasswordAuthenticationToken("admin", "admin"))
        assertTrue(authResult.isAuthenticated)
    }
}