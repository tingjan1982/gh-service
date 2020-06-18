package io.geekhub.service.auth0.service.bean

import com.fasterxml.jackson.annotation.JsonAlias

data class Auth0UserResponse(@field:JsonAlias("user_id") val userId: String,
                             val email: String,
                             val name: String,
                             val nickname: String,
                             val picture: String,
                             @field:JsonAlias("user_metadata") val userMetadata: Map<String, Any>?)
