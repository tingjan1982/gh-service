package io.geekhub.service.auth0.service

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import io.geekhub.service.account.web.model.UpdateClientUserRequest
import io.geekhub.service.account.web.model.UpdateUserPasswordRequest
import io.geekhub.service.auth0.service.bean.Auth0User
import io.geekhub.service.auth0.service.bean.Auth0UserResponse
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.exception.BusinessObjectNotFoundException
import org.apache.http.client.config.CookieSpecs
import org.apache.http.client.config.RequestConfig
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
class Auth0ManagementServiceImpl(val managementApiProperties: Auth0ManagementApiProperties, val apiConfigProperties: Auth0ApiProperties) : Auth0ManagementService {

    val restTemplate: RestTemplate
        get() {
            val httpClient: CloseableHttpClient = HttpClients.custom()
                    .setSSLHostnameVerifier(NoopHostnameVerifier())
                    .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                    .build()
            val requestFactory = HttpComponentsClientHttpRequestFactory()
            requestFactory.httpClient = httpClient

            return RestTemplate(requestFactory)
        }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(Auth0ManagementServiceImpl::class.java)
    }

    //@Cacheable("managementToken")
    override fun getManagementToken(): OAuthToken {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val requestParams = mapOf("grant_type" to "client_credentials",
                "client_id" to managementApiProperties.clientId,
                "client_secret" to managementApiProperties.secret,
                "audience" to managementApiProperties.audience)
        val requestEntity = HttpEntity(requestParams, headers)

        restTemplate.postForEntity("https://geekhub.auth0.com/oauth/token", requestEntity, OAuthToken::class.java).let { response ->
            return response.body
                    ?: throw BusinessException("Failed to obtain management token")
        }
    }

    override fun getUserToken(email: String, password: String): OAuthToken {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val requestParams = mapOf("grant_type" to "password",
                "username" to email,
                "password" to password,
                "client_id" to apiConfigProperties.clientId,
                "client_secret" to apiConfigProperties.secret,
                "audience" to apiConfigProperties.audience,
                "scope" to "openid profile email read:profile")
        val requestEntity = HttpEntity(requestParams, headers)

        try {
            restTemplate.postForEntity("https://geekhub.auth0.com/oauth/token", requestEntity, OAuthToken::class.java).let { response ->
                return response.body ?: throw BusinessException("Failed to obtain user token")
            }
        } catch (ex: HttpClientErrorException) {
            throw BusinessException("The provided credentials account cannot be authenticated.")
        }
    }

    override fun createUser(user: Auth0User, oAuthToken: OAuthToken): Auth0UserResponse {

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.setBearerAuth(oAuthToken.accessToken)
        val requestEntity = HttpEntity(user, headers)

        restTemplate.postForEntity("https://geekhub.auth0.com/api/v2/users", requestEntity,
                Auth0UserResponse::class.java).let { response ->

            if (response.statusCode == HttpStatus.CONFLICT) {
                throw BusinessException("The user already exists")
            }

            response.body?.let { userInfo ->
                return userInfo

            } ?: throw BusinessException("Error creating new auth0 user")
        }
    }

    override fun getUser(userId: String, oAuthToken: OAuthToken): Auth0UserResponse {

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.setBearerAuth(oAuthToken.accessToken)
        val requestEntity = HttpEntity<Void>(headers)

        restTemplate.exchange("https://geekhub.auth0.com/api/v2/users/$userId", HttpMethod.GET, requestEntity,
                Auth0UserResponse::class.java).let { response ->

            response.body?.let { userInfo ->
                return userInfo

            } ?: throw BusinessObjectNotFoundException(Auth0UserResponse::class, userId)
        }
    }

    override fun updateUser(userId: String, updateUserRequest: UpdateUserRequest, oAuthToken: OAuthToken): Auth0UserResponse {

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.setBearerAuth(oAuthToken.accessToken)
        val requestEntity = HttpEntity(updateUserRequest, headers)

        LOGGER.info("Updating user info, id=$userId")

        restTemplate.exchange("https://geekhub.auth0.com/api/v2/users/$userId", HttpMethod.PATCH, requestEntity,
                Auth0UserResponse::class.java).let { response ->

            response.body?.let { userInfo ->
                return userInfo
            } ?: throw BusinessObjectNotFoundException(Auth0UserResponse::class, userId)
        }
    }

    override fun updateUserPassword(updateRequest: UpdateUserPasswordRequest, oAuthToken: OAuthToken) {

        getUserToken(updateRequest.email, updateRequest.oldPassword).let {
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            headers.setBearerAuth(oAuthToken.accessToken)
            val requestEntity = HttpEntity(UpdatePasswordRequest(updateRequest.newPassword), headers)

            LOGGER.info("Updating user password, id=${updateRequest.userId}")

            try {
                restTemplate.exchange("https://geekhub.auth0.com/api/v2/users/${updateRequest.userId}", HttpMethod.PATCH, requestEntity,
                        Auth0UserResponse::class.java).let { response ->

                    println(response.statusCode)

                    response.body?.let { userInfo ->
                        println(userInfo)
                    }
                }
            } catch (ex: HttpClientErrorException) {
                throw BusinessException(ex.localizedMessage)
            }
        }
    }

    override fun deleteUser(userId: String, oAuthToken: OAuthToken) {

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.setBearerAuth(oAuthToken.accessToken)
        val requestEntity = HttpEntity<Void>(headers)

        restTemplate.exchange("https://geekhub.auth0.com/api/v2/users/${userId}", HttpMethod.DELETE, requestEntity, String::class.java).let { response ->
            if (response.statusCode != HttpStatus.NO_CONTENT) {
                throw BusinessException("Error deleting the user")
            }
        }
    }

    data class OAuthToken(@field:JsonAlias("access_token") val accessToken: String,
                          @field:JsonAlias("scope") val scope: String,
                          @field:JsonAlias("expires_in") val expireInSeconds: Int)

    data class UpdateUserRequest(val name: String,
                                 val nickname: String?,
                                 @field:JsonProperty("user_metadata") val userMetadata: Map<String, Any>,
                                 val connection: String = "Username-Password-Authentication")

    data class UpdatePasswordRequest(val password: String, val connection: String = "Username-Password-Authentication")
}

fun UpdateClientUserRequest.toUpdateUserRequest() = Auth0ManagementServiceImpl.UpdateUserRequest(
        name = this.name,
        nickname = this.nickname,
        userMetadata = mapOf(
                "companyName" to (this.companyName ?: ""),
                "note" to (this.note ?: ""),
                "socialProfiles" to this.toSocialProfiles()
        )
)