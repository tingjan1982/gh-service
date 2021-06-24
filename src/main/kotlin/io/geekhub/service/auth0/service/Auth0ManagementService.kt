package io.geekhub.service.auth0.service

import io.geekhub.service.account.web.model.UpdateUserPasswordRequest
import io.geekhub.service.auth0.service.bean.Auth0User
import io.geekhub.service.auth0.service.bean.Auth0UserResponse

interface Auth0ManagementService {

    fun getManagementToken(): Auth0ManagementServiceImpl.OAuthToken

    fun getUserToken(email: String, password: String): Auth0ManagementServiceImpl.OAuthToken

    fun createUser(user: Auth0User, oAuthToken: Auth0ManagementServiceImpl.OAuthToken): Auth0UserResponse

    fun getUser(userId: String, oAuthToken: Auth0ManagementServiceImpl.OAuthToken): Auth0UserResponse

    fun updateUser(userId: String, updateUserRequest: Auth0ManagementServiceImpl.UpdateUserRequest, oAuthToken: Auth0ManagementServiceImpl.OAuthToken): Auth0UserResponse

    fun addRoleToUser(userId: String, roleId: String, oAuthToken: Auth0ManagementServiceImpl.OAuthToken)

    fun removeRoleFromUser(userId: String, roleId: String, oAuthToken: Auth0ManagementServiceImpl.OAuthToken)

    fun updateUserPassword(updateRequest: UpdateUserPasswordRequest, oAuthToken: Auth0ManagementServiceImpl.OAuthToken)

    fun deleteUser(userId: String, oAuthToken: Auth0ManagementServiceImpl.OAuthToken)
}