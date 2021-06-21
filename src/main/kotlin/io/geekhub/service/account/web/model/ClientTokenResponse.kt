package io.geekhub.service.account.web.model

data class ClientTokenResponse(
    val accessToken: String,
    val userKey: String
)
