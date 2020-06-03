package io.geekhub.service.auth0.service

import io.geekhub.service.auth0.service.bean.Auth0User
import io.geekhub.service.auth0.service.bean.Auth0UserResponse

interface Auth0ManagementService {

    fun getManagementToken(): Auth0ManagementServiceImpl.OAuthToken

    fun getUserToken(email: String, password: String): Auth0ManagementServiceImpl.OAuthToken

    fun createUser(user: Auth0User, oAuthToken: Auth0ManagementServiceImpl.OAuthToken): Auth0UserResponse

    fun getUser(userId: String, oAuthToken: Auth0ManagementServiceImpl.OAuthToken): Auth0UserResponse

    fun updateUserPassword(userId: String, updatedPassword: String, oAuthToken: Auth0ManagementServiceImpl.OAuthToken)

    fun deleteUser(userId: String, oAuthToken: Auth0ManagementServiceImpl.OAuthToken)
}