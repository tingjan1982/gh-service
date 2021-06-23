package io.geekhub.service.account.web

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.account.service.ClientAccountService
import io.geekhub.service.account.service.ClientUserService
import io.geekhub.service.account.web.model.ClientOrganizationResponse
import io.geekhub.service.account.web.model.EnableOrganizationRequest
import io.geekhub.service.account.web.model.OrganizationRequest
import io.geekhub.service.account.web.model.OrganizationUserRequest
import io.geekhub.service.binarystorage.service.BinaryStorageService
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.extensions.toLightDTO
import io.geekhub.service.shared.extensions.toOrganization
import io.geekhub.service.shared.web.filter.ClientAccountFilter.Companion.CLIENT_USER_KEY
import org.springframework.http.HttpStatus
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping("/organizations")
class ClientOrganizationController(val clientAccountService: ClientAccountService,
                                   val clientUserService: ClientUserService,
                                   val binaryStorageService: BinaryStorageService) {

    @GetMapping("/{id:[\\w|]+}")
    fun getOrganization(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                        @PathVariable id: String): ClientOrganizationResponse {

        return clientAccountService.getClientOrganizationAccount(id).let { acc ->
            val users = clientUserService.getClientUsers(acc).map { it.toLightDTO() }.toList()

            acc.toOrganization(users)
        }
    }

    @GetMapping("/me")
    fun getOrganization(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser): ClientOrganizationResponse {

        if (clientUser.clientAccount.accountType == ClientAccount.AccountType.INDIVIDUAL) {
            throw BusinessException("User has no organization")
        }

        clientUser.clientAccount.let { acc ->
            val users = clientUserService.getClientUsers(acc).map { it.toLightDTO() }.toList()

            return acc.toOrganization(users)
        }
    }

    @PostMapping("/{id:[\\w|]+}")
    fun updateOrganization(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                           @PathVariable id: String,
                           @Valid @RequestBody request: OrganizationRequest): ClientOrganizationResponse {

        clientAccountService.getClientOrganizationAccount(id).let { acc ->
            acc.clientName = request.name
            clientAccountService.saveClientAccount(acc)
            val users = clientUserService.getClientUsers(acc).map { it.toLightDTO() }.toList()

            return acc.toOrganization(users)
        }
    }

    @PostMapping("/me/enable")
    fun enableOrganization(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                           @Valid @RequestBody request: EnableOrganizationRequest): ClientOrganizationResponse {

        clientAccountService.enableOrganization(clientUser, request.organizationName).let {
            return it.toOrganization()
        }
    }

//    @PostMapping("/me/owner")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    fun changeOrganizationOwner(
//        @RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
//        @RequestBody request: ChangeOrganizationOwnerRequest
//    ) {
//
//        clientUserService.getClientUser(request.clientUserId).let {
//            clientAccountService.changeOrganizationOwner(clientUser, it)
//        }
//    }

    @DeleteMapping("/me/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun leaveOrganization(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser) {

        clientAccountService.leaveOrganization(clientUser)
    }

    @PostMapping("/{id:[\\w|]+}/invitations")
    fun inviteUser(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                   @PathVariable id: String,
                   @Valid @RequestBody request: OrganizationUserRequest): ClientOrganizationResponse {

        clientAccountService.getClientOrganizationAccount(id).let { acc ->
            clientAccountService.inviteOrganizationUser(clientUser, acc, request.email).let {

                val users = clientUserService.getClientUsers(acc).map { it.toLightDTO() }.toList()
                return it.toOrganization(users)
            }
        }
    }

    @DeleteMapping("/{id:[\\w|]+}/invitations")
    fun uninviteUser(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                     @PathVariable id: String,
                     @Valid @RequestBody request: OrganizationUserRequest): ClientOrganizationResponse {

        clientAccountService.getClientOrganizationAccount(id).let { acc ->
            val users = clientUserService.getClientUsers(acc).map { it.toLightDTO() }.toList()
            return clientAccountService.uninviteOrganizationUser(clientUser, acc, request.email).toOrganization(users)
        }
    }

    @PostMapping("/{id:[\\w|]+}/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun joinOrganization(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                         @PathVariable id: String) {

        clientAccountService.getClientAccount(id).let {
            clientAccountService.joinOrganization(clientUser, it)
        }
    }

    @PostMapping("/{id:[\\w|]+}/decline")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun declineOrganization(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                            @PathVariable id: String) {

        clientAccountService.getClientAccount(id).let {
            clientAccountService.userDeclineOrganizationInvitation(clientUser, it)
        }
    }

    @DeleteMapping("/{id:[\\w|]+}/users/{userId:[\\w|]+}")
    fun removeClientUserFromOrganization(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                                         @PathVariable id: String,
                                         @PathVariable userId: String) {

        clientUserService.getClientUser(userId).let {
            clientAccountService.leaveOrganization(it)
        }
    }

    @GetMapping("/{id:[\\w|]+}/avatar")
    fun getOrganizationAvatar(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                              @PathVariable id: String,
                              response: HttpServletResponse) {

        renderUserAvatar(clientAccountService.getClientOrganizationAccount(id), response)
    }

    private fun renderUserAvatar(clientAccount: ClientAccount, response: HttpServletResponse) {

        clientAccount.avatarBinary?.let {
            FileCopyUtils.copy(it.binary.data, response.outputStream);
        } ?: run {
            response.status = 204
        }
    }

    @PostMapping("/{id:[\\w|]+}/avatar")
    fun uploadOrganizationAvatar(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                                 @PathVariable id: String,
                                 @RequestParam("file") multipartFile: MultipartFile): ClientOrganizationResponse {

        clientAccountService.getClientOrganizationAccount(id).let {
            val users = clientUserService.getClientUsers(it).map { it.toLightDTO() }.toList()

            return binaryStorageService.saveClientAccountAvatar(it, multipartFile).toOrganization(users)
        }
    }
}