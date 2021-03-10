package io.geekhub.service.account.web

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.account.service.ClientAccountService
import io.geekhub.service.account.service.ClientUserService
import io.geekhub.service.account.web.model.ClientOrganizationResponse
import io.geekhub.service.account.web.model.OrganizationUserRequest
import io.geekhub.service.shared.extensions.toLightDTO
import io.geekhub.service.shared.extensions.toOrganization
import io.geekhub.service.shared.web.filter.ClientAccountFilter.Companion.CLIENT_USER_KEY
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/organizations")
class ClientOrganizationController(val clientAccountService: ClientAccountService, val clientUserService: ClientUserService) {

    @GetMapping("/{id:[\\w|]+}")
    fun getOrganization(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                        @PathVariable id: String): ClientOrganizationResponse {

        return clientAccountService.getClientOrganizationAccount(id).let { acc ->
            val users = clientUserService.getClientUsers(acc).map { it.toLightDTO() }.toList()

            acc.toOrganization(users)
        }
    }

    @PostMapping("/{id:[\\w|]+}/invitations")
    fun inviteClientUser(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                         @PathVariable id: String,
                         @Valid @RequestBody request: OrganizationUserRequest): ClientOrganizationResponse {

        clientAccountService.getClientOrganizationAccount(id).let { acc ->
            val users = clientUserService.getClientUsers(acc).map { it.toLightDTO() }.toList()
            return clientAccountService.inviteOrganizationUser(clientUser, acc, request.email).toOrganization(users)
        }
    }

    @DeleteMapping("/{id:[\\w|]+}/invitations")
    fun uninviteClientUser(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                           @PathVariable id: String,
                           @Valid @RequestBody request: OrganizationUserRequest): ClientOrganizationResponse {

        clientAccountService.getClientOrganizationAccount(id).let { acc ->
            val users = clientUserService.getClientUsers(acc).map { it.toLightDTO() }.toList()
            return clientAccountService.uninviteOrganizationUser(clientUser, acc, request.email).toOrganization(users)
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
}