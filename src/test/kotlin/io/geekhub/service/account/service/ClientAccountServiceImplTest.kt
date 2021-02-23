package io.geekhub.service.account.service

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientUser
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
                                            @Autowired val clientUserService: ClientUserService,
                                            @Autowired val auth0ManagementService: Auth0ManagementService) {

    lateinit var oauthToken: Auth0ManagementServiceImpl.OAuthToken

    lateinit var user: Auth0UserResponse

    lateinit var clientUser: ClientUser

    @BeforeEach
    fun prepare() {
        auth0ManagementService.getManagementToken().let {
            this.oauthToken = it

            auth0ManagementService.createUser(Auth0User(email = "integration-test@geekhub.tw", password = "%90Stuauth0"), it).let { user ->
                this.user = user

                ClientAccount(user.userId, ClientAccount.AccountType.INDIVIDUAL, ClientAccount.PlanType.FREE, user.name).let { account ->
                    clientAccountService.saveClientAccount(account)

                    ClientUser(user.userId, user.email, user.name, user.nickname, user.picture, ClientUser.UserType.AUTH0, true, account).let { user ->
                        clientUser = clientUserService.saveClientUser(user)
                    }
                }
            }
        }
    }

    @AfterEach
    fun deleteUser() {
        auth0ManagementService.deleteUser(user.userId, oauthToken)
    }

    @Test
    fun `enable organization and invite users`() {

        val orgName = "GeekHub"
        clientAccountService.enableOrganization(clientUser, orgName).also {
            assertThat(it.accountType).isEqualTo(ClientAccount.AccountType.CORPORATE)
            assertThat(it.clientName).isEqualTo(orgName)
        }

        clientAccountService.inviteOrganizationUser(clientUser, "user1@gmail.com").also {
            assertThat(it.userInvitations).hasSize(1)
        }

        clientAccountService.uninviteOrganizationUser(clientUser, "user1@gmail.com").also {
            assertThat(it.userInvitations).isEmpty()
        }
    }
}