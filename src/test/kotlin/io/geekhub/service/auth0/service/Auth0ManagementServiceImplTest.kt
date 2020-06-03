package io.geekhub.service.auth0.service

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isNotNull
import assertk.assertions.prop
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

                auth0ManagementService.getUser(user.userId, it).run {
                    assertThat(this.userId).isEqualTo(user.userId)
                }

                auth0ManagementService.updateUserPassword(user.userId, "integration1", it)
                
                auth0ManagementService.getUserToken(user.email, "integration1").run {
                    println(this)

                    assertThat(this.accessToken).isNotNull()
                }
            }
        }
    }

}