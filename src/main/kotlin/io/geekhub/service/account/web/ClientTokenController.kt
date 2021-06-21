package io.geekhub.service.account.web

import io.geekhub.service.account.web.model.ClientTokenResponse
import io.geekhub.service.auth0.service.Auth0ManagementService
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tokens")
class ClientTokenController(val auth0ManagementService: Auth0ManagementService) {

    @PostMapping
    fun getClientToken(
        @RequestParam("username") username: String,
        @RequestParam("password") password: String
    ): ClientTokenResponse {
        auth0ManagementService.getUserToken(username, password).let {

            val userKey = RandomValueStringGenerator(32).generate()
            return ClientTokenResponse(it.accessToken, userKey)
        }
    }
}