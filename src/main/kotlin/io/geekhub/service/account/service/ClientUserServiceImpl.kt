package io.geekhub.service.account.service

import io.geekhub.service.account.repository.*
import io.geekhub.service.auth0.service.Auth0ManagementService
import io.geekhub.service.shared.annotation.TransactionSupport
import io.geekhub.service.shared.exception.BusinessObjectNotFoundException
import io.geekhub.service.shared.token.NeedToRefreshTokenHolder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
@TransactionSupport
class ClientUserServiceImpl(val clientUserRepository: ClientUserRepository, val auth0ManagementService: Auth0ManagementService) : ClientUserService {

    val restTemplate = RestTemplate()

    override fun saveClientUser(clientUser: ClientUser): ClientUser {
        return clientUserRepository.save(clientUser)
    }

    override fun getClientUser(id: String): ClientUser {
        return clientUserRepository.findById(id).orElseThrow {
            throw BusinessObjectNotFoundException(ClientUser::class, id)
        }
    }

    override fun getClientUserByEmail(email: String): ClientUser? {
        return clientUserRepository.findByEmail(email)
    }

    override fun lookupClientUser(id: String): ClientUser? {
        return clientUserRepository.findById(id).orElse(null)
    }

    override fun getClientAccountOwner(clientAccount: ClientAccount): ClientUser? {
        return clientUserRepository.findByClientAccountAndAccountPrivilege(clientAccount, ClientUser.AccountPrivilege.OWNER)
    }

    /**
     * todo: check that the passed in token contains the required scope: openid profile email
     *
     * Populate user metadata in id token in Auth0 rule:
     * https://auth0.com/docs/manage-users/user-accounts/metadata
     * https://community.auth0.com/t/how-to-get-user-metadata-and-app-metadata-in-id-token/20898
     */
    override fun getAuth0UserInfo(token: String): Auth0UserInfo {

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.setBearerAuth(token)
        val requestEntity = HttpEntity<Void>(headers)

        restTemplate.exchange("https://geekhub.auth0.com/userinfo", HttpMethod.GET, requestEntity, Auth0UserInfo::class.java)
            .let { response ->
                response.body?.let { userInfo ->
                    return userInfo

                } ?: throw BusinessObjectNotFoundException(Auth0UserInfo::class, token)
            }
    }

    override fun getClientUsers(clientAccount: ClientAccount): List<ClientUser> {
        return clientUserRepository.findAllByClientAccount(clientAccount)
    }

    override fun clientUsersExistInDepartment(department: ClientDepartment): Boolean {
        return clientUserRepository.existsByDepartment(department)
    }

    override fun clientUserExists(organization: ClientAccount, email: String): Boolean {
        return clientUserRepository.existsByClientAccountAndEmail(organization, email)
    }

    override fun addOwnerRole(clientUser: ClientUser) {

        auth0ManagementService.getManagementToken().let { token ->
            auth0ManagementService.addRoleToUser(clientUser.id.toString(), "rol_2kVbpqyNmJ0qGYRs", token)

            NeedToRefreshTokenHolder.addSecurityContextToken()
        }
    }

    override fun removeOwnerRole(clientUser: ClientUser) {

        auth0ManagementService.getManagementToken().let { token ->
            auth0ManagementService.removeRoleFromUser(clientUser.id.toString(), "rol_2kVbpqyNmJ0qGYRs", token)

            NeedToRefreshTokenHolder.addSecurityContextToken()
        }
    }
}