package io.geekhub.service.auth0.service

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import io.geekhub.service.account.web.model.UpdateClientUserRequest
import io.geekhub.service.account.web.model.UpdateUserPasswordRequest
import io.geekhub.service.auth0.service.bean.Auth0User
import io.geekhub.service.auth0.service.bean.Auth0UserResponse
import io.geekhub.service.shared.annotation.IntegrationTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
internal class Auth0ManagementServiceImplTest(@Autowired val auth0ManagementService: Auth0ManagementService) {

    lateinit var oAuthToken: Auth0ManagementServiceImpl.OAuthToken

    lateinit var user: Auth0UserResponse

    @AfterEach
    fun deleteUsr() {
        auth0ManagementService.deleteUser(user.userId, oAuthToken)
    }

    @Test
    fun manageAuth0User() {

        auth0ManagementService.getManagementToken().let {
            oAuthToken = it

            assertThat(it).all {
                prop(Auth0ManagementServiceImpl.OAuthToken::accessToken).isNotNull()
                prop(Auth0ManagementServiceImpl.OAuthToken::scope).isNotNull()
                prop(Auth0ManagementServiceImpl.OAuthToken::expireInSeconds).isGreaterThan(0)
            }

            it
        }.let {
            auth0ManagementService.createUser(Auth0User(email = "integration-test@geekhub.tw", password = "integration"), it).let { user ->
                println(user)
                this.user = user

                auth0ManagementService.addRoleToUser(user.userId, "rol_2kVbpqyNmJ0qGYRs", it)

                Auth0ManagementServiceImpl.UpdateUserRequest(name = "integration-test",
                        nickname = "int",
                        userMetadata = mapOf(
                                "companyName" to "geekhub",
                                "note" to "this is a brief description that i have added to describe me",
                                "socialProfiles" to listOf(
                                        UpdateClientUserRequest.SocialProfile("linkedIn", "https://linkedin.com/profile/integration-test")
                                )
                        )).let { request ->
                    auth0ManagementService.updateUser(user.userId, request, it)
                }

                auth0ManagementService.getUser(user.userId, it).run {
                    assertThat(this.userId).isEqualTo(user.userId)
                    assertThat(this.name).isEqualTo("integration-test")
                    assertThat(this.nickname).isEqualTo("int")
                    assertThat(this.userMetadata!!).hasSize(3)
                }

                UpdateUserPasswordRequest("integration", "integration1").apply {
                    userId = user.userId
                    email = user.email
                }.let { request ->
                    auth0ManagementService.updateUserPassword(request, it)
                }

                auth0ManagementService.getUserToken(user.email, "integration1").run {
                    println(this)

                    assertThat(this.accessToken).isNotNull()
                }

                auth0ManagementService.removeRoleFromUser(user.userId, "rol_2kVbpqyNmJ0qGYRs", it)
            }
        }
    }
}