package io.geekhub.service.account.web

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.account.service.ClientUserService
import io.geekhub.service.account.web.model.ClientUserResponse
import io.geekhub.service.account.web.model.UpdateClientUserRequest
import io.geekhub.service.auth0.service.Auth0ManagementService
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.service.InterviewService
import io.geekhub.service.interview.web.model.InterviewsResponse
import io.geekhub.service.likes.service.LikeService
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

    @GetMapping("/me")
    fun getUserProfile(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser): ClientUserResponse {
        return clientUser.toDTO()
    }

    @PostMapping("/me")
    fun updateUserProfile(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                          @Valid @RequestBody updateClientUserRequest: UpdateClientUserRequest): ClientUserResponse {

        auth0ManagementService.getManagementToken().let {
            auth0ManagementService.updateUser(clientUser.id.toString(), updateClientUserRequest, it)

            clientUser.apply {
                this.name = updateClientUserRequest.name
                this.nickname = updateClientUserRequest.nickname

                return clientUserService.saveClientUser(this).toDTO()
            }
        }
    }

    @PostMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateUserPassword(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                           @RequestBody password: String) {

        auth0ManagementService.getManagementToken().let {
            auth0ManagementService.updateUserPassword(clientUser.id.toString(), password, it)
        }
    }

    @GetMapping("/me/likedInterviews")
    fun getLikedInterviews(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                           @RequestParam("page", defaultValue = "0") page: Int,
                           @RequestParam("pageSize", defaultValue = "20") pageSize: Int,
                           uriComponentsBuilder: UriComponentsBuilder): InterviewsResponse {

        val pageRequest = PageRequest.of(page, pageSize)
        likeService.getLikedObjectsAsType(clientUser, Interview::class, pageRequest).let { result ->
            val navigationLinkBuilder = uriComponentsBuilder.path("/interviews").let {
                it.queryParam("page", page)
                it.queryParam("pageSize", pageSize)

                it
            }

            return InterviewsResponse(result.map { it.toLightDTO(true) }, navigationLinkBuilder)
        }
    }
}