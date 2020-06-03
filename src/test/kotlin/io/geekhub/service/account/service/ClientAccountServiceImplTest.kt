package io.geekhub.service.account.service

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.auth0.service.Auth0ManagementService
import io.geekhub.service.auth0.service.Auth0ManagementServiceImpl
import io.geekhub.service.auth0.service.bean.Auth0User
import io.geekhub.service.auth0.service.bean.Auth0UserResponse
import io.geekhub.service.shared.annotation.IntegrationTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
internal class ClientAccountServiceImplTest(@Autowired val clientAccountService: ClientAccountService,
                                            @Autowired val auth0ManagementService: Auth0ManagementService) {

    lateinit var oauthToken: Auth0ManagementServiceImpl.OAuthToken

    lateinit var user: Auth0UserResponse

    @BeforeEach
    fun prepare() {
        auth0ManagementService.getManagementToken().let {
            this.oauthToken = it

            auth0ManagementService.createUser(Auth0User(email = "integration-test@geekhub.tw", password = "%90Stuauth0"), it).let { user ->
                this.user = user
            }
        }
    }

    @AfterEach
    fun deleteUser() {
        auth0ManagementService.deleteUser(user.userId, oauthToken)
    }

    @Test
    fun updatePassword() {

        ClientAccount(id = user.userId, email = user.email, accountType = ClientAccount.AccountType.CORPORATE, clientName = user.email).let {
            clientAccountService.updatePassword(it, "%%90Stuauth0")
        }
    }
}