package io.geekhub.service.auth0.service.bean

import com.fasterxml.jackson.annotation.JsonAlias

data class Auth0UserResponse(@field:JsonAlias("user_id") val userId: String,
                             val email: String)
