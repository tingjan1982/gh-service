package io.geekhub.service.account.web

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.account.service.ClientAccountService
import io.geekhub.service.account.service.ClientDepartmentService
import io.geekhub.service.account.service.ClientUserService
import io.geekhub.service.account.web.model.*
import io.geekhub.service.auth0.service.Auth0ManagementService
import io.geekhub.service.auth0.service.toUpdateUserRequest
import io.geekhub.service.binarystorage.service.BinaryStorageService
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.service.InterviewService
import io.geekhub.service.interview.web.model.InterviewsResponse
import io.geekhub.service.likes.service.LikeService
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.shared.extensions.toLightDTO
import io.geekhub.service.shared.extensions.toOrganization
import io.geekhub.service.shared.model.SearchCriteria
import io.geekhub.service.shared.web.filter.ClientAccountFilter.Companion.CLIENT_USER_KEY
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.util.UriComponentsBuilder
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping("/users")
class ClientUserController(val clientUserService: ClientUserService,
                           val clientAccountService: ClientAccountService,
                           val clientDepartmentService: ClientDepartmentService,
                           val interviewService: InterviewService,
                           val likeService: LikeService,
                           val auth0ManagementService: Auth0ManagementService,
                           val binaryStorageService: BinaryStorageService) {

    @GetMapping("/me")
    fun getMyProfile(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser): ClientUserResponse {

        auth0ManagementService.getManagementToken().let { token ->
            auth0ManagementService.getUser(clientUser.id.toString(), token).let { auth0User ->
                val invitations = clientAccountService.getInvitedCorporateAccounts(clientUser.email).map { it.toDTO() }.toList()

                return clientUser.toDTO(auth0User.userMetadata, invitations)
            }
        }
    }

    @GetMapping("/{id:[\\w|]+}")
    fun getUserProfile(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                       @PathVariable id: String): ClientUserResponse {

        clientUserService.getClientUser(id).let {
            auth0ManagementService.getManagementToken().let { token ->
                auth0ManagementService.getUser(id, token).let { auth0User ->
                    return it.toDTO(auth0User.userMetadata)
                }
            }
        }
    }

    @PostMapping("/me")
    fun updateUserProfile(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                          @Valid @RequestBody updateClientUserRequest: UpdateClientUserRequest): ClientUserResponse {

        auth0ManagementService.getManagementToken().let {
            updateClientUserRequest.toUpdateUserRequest().let { updateRequest ->
                val updatedUser = auth0ManagementService.updateUser(clientUser.id.toString(), updateRequest, it)

                clientUser.apply {
                    this.name = updateClientUserRequest.name
                    this.nickname = updateClientUserRequest.nickname

                    return clientUserService.saveClientUser(this).toDTO(updatedUser.userMetadata)
                }
            }
        }
    }

    @GetMapping("/me/organization")
    fun getOrganization(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser): ClientOrganizationResponse {

        clientUser.clientAccount.let { acc ->
            val users = clientUserService.getClientUsers(acc).map { it.toLightDTO() }.toList()

            return acc.toOrganization(users)
        }
    }

    @PostMapping("/me/organization")
    fun enableOrganization(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                           @Valid @RequestBody request: EnableOrganizationRequest): ClientUserResponse {

        clientAccountService.enableOrganization(clientUser, request.organizationName)
        return clientUser.toDTO()
    }

    @PostMapping("/me/organization/join")
    fun joinOrganization(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                         @Valid @RequestBody request: JoinOrganizationRequest): ClientUserResponse {

        clientAccountService.getClientAccount(request.organizationId).let {
            clientAccountService.joinOrganization(clientUser, it)

            return clientUser.toDTO()
        }
    }

    @DeleteMapping("/me/organization")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun leaveOrganization(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser) {

        clientAccountService.leaveOrganization(clientUser)
    }

    @PostMapping("/{id:[\\w|]+}/department")
    fun assignUserToDepartment(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                               @PathVariable id: String,
                               @Valid @RequestBody request: AssignDepartmentRequest): ClientUserResponse {

        val user = clientUserService.getClientUser(id)
        clientDepartmentService.getDepartment(request.departmentId).let {
            user.department = it
            return clientUserService.saveClientUser(user).toDTO()
        }
    }

    @DeleteMapping("/{id:[\\w|]+}/department")
    fun removeUserFromDepartment(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                                 @PathVariable id: String): ClientUserResponse {

        clientUserService.getClientUser(id).let {
            it.department = null
            return clientUserService.saveClientUser(it).toDTO()
        }
    }

    @GetMapping("/me/avatar")
    fun getAvatar(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                  response: HttpServletResponse) {

        clientUser.avatarBinary?.let {
            FileCopyUtils.copy(it.binary.data, response.outputStream);
        } ?: run {
            response.status = 204
        }
    }

    @PostMapping("/me/avatar")
    fun uploadAvatar(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                     @RequestParam("file") multipartFile: MultipartFile): ClientUserResponse {

        return binaryStorageService.saveClientUserAvatar(clientUser, multipartFile).toDTO()
    }

    @DeleteMapping("/me/avatar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAvatar(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser) {

        clientUser.avatarBinary?.let {
            binaryStorageService.deleteBinary(it)
            clientUser.avatarBinary = null
            clientUserService.saveClientUser(clientUser)
        }
    }

    @PostMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateUserPassword(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                           @RequestBody updatePasswordRequest: UpdateUserPasswordRequest) {

        if (clientUser.userType != ClientUser.UserType.AUTH0) {
            throw BusinessException("Update password is not available to non AUTH0 users")
        }

        updatePasswordRequest.apply {
            userId = clientUser.id.toString()
            email = clientUser.email
        }

        auth0ManagementService.getManagementToken().let {
            auth0ManagementService.updateUserPassword(updatePasswordRequest, it)
        }
    }

    @GetMapping("/{id:[\\w|]+}/ownedInterviews")
    fun getOwnedInterviews(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                           @PathVariable id: String,
                           @RequestParam("page", defaultValue = "0") page: Int,
                           @RequestParam("pageSize", defaultValue = "20") pageSize: Int,
                           uriComponentsBuilder: UriComponentsBuilder): InterviewsResponse {

        clientUserService.getClientUser(id).let {
            val requestMap = mapOf("owner" to "true", "page" to page.toString(), "pageSize" to pageSize.toString())
            interviewService.getInterviews(SearchCriteria.fromRequestParameters(clientUser, requestMap)).let { result ->
                val navigationLinkBuilder = uriComponentsBuilder.path("/interviews").let {
                    requestMap.forEach { entry ->
                        it.queryParam(entry.key, entry.value)
                    }

                    it
                }

                return InterviewsResponse(result.map { it.toLightDTO() }, navigationLinkBuilder)
            }
        }
    }

    @GetMapping("/{id:[\\w|]+}/likedInterviews")
    fun getLikedInterviews(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                           @PathVariable id: String,
                           @RequestParam("page", defaultValue = "0") page: Int,
                           @RequestParam("pageSize", defaultValue = "20") pageSize: Int,
                           uriComponentsBuilder: UriComponentsBuilder): InterviewsResponse {

        clientUserService.getClientUser(id).let {
            val pageRequest = PageRequest.of(page, pageSize)
            likeService.getLikedObjectsAsType(it, Interview::class, pageRequest).let { result ->
                val navigationLinkBuilder = uriComponentsBuilder.path("/interviews").let { uriBuilder ->
                    uriBuilder.queryParam("page", page)
                    uriBuilder.queryParam("pageSize", pageSize)

                    uriBuilder
                }

                return InterviewsResponse(result.map { i -> i.toLightDTO(true) }, navigationLinkBuilder)
            }
        }

    }
}