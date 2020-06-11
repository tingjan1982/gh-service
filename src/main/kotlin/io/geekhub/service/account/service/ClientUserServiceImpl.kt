package io.geekhub.service.account.service

import io.geekhub.service.account.repository.Auth0UserInfo
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.account.repository.ClientUserRepository
import io.geekhub.service.shared.exception.BusinessObjectNotFoundException
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import javax.transaction.Transactional

@Service
@Transactional
class ClientUserServiceImpl(val clientUserRepository: ClientUserRepository) : ClientUserService {

    val restTemplate = RestTemplate()

    override fun saveClientUser(clientUser: ClientUser): ClientUser {
        return clientUserRepository.save(clientUser)
    }

    override fun getClientUser(id: String): ClientUser? {
        return clientUserRepository.findById(id).orElse(null)
    }

    /**
     * todo: check that the passed in token contains the required scope: openid profile email
     */
    override fun getAuth0UserInfo(token: String): Auth0UserInfo {

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.setBearerAuth(token)
        val requestEntity = HttpEntity<Void>(headers)

        restTemplate.exchange("https://geekhub.auth0.com/userinfo", HttpMethod.GET, requestEntity, Auth0UserInfo::class.java).let { response ->
            response.body?.let { userInfo ->
                return userInfo

            } ?: throw BusinessObjectNotFoundException(Auth0UserInfo::class, token)
        }
    }
}