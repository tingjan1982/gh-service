package io.geekhub.service.account.web

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.account.service.ClientUserService
import io.geekhub.service.account.web.model.ClientUserResponse
import io.geekhub.service.account.web.model.UpdateClientUserRequest
import io.geekhub.service.account.web.model.UpdateUserPasswordRequest
import io.geekhub.service.auth0.service.Auth0ManagementService
import io.geekhub.service.auth0.service.toUpdateUserRequest
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.service.InterviewService
import io.geekhub.service.interview.web.model.InterviewsResponse
import io.geekhub.service.likes.service.LikeService
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.shared.extensions.toLightDTO
import io.geekhub.service.shared.web.filter.ClientAccountFilter.Companion.CLIENT_USER_KEY
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import javax.validation.Valid

@RestController
@RequestMapping("/users")
class ClientUserController(val clientUserService: ClientUserService,
                           val interviewService: InterviewService,
                           val likeService: LikeService,
                           val auth0ManagementService: Auth0ManagementService) {

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