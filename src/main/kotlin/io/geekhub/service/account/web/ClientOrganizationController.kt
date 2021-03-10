package io.geekhub.service.account.web

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.account.service.ClientAccountService
import io.geekhub.service.account.web.model.ClientOrganizationResponse
import io.geekhub.service.account.web.model.OrganizationUserRequest
import io.geekhub.service.shared.extensions.toOrganization
import io.geekhub.service.shared.web.filter.ClientAccountFilter.Companion.CLIENT_USER_KEY
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/organizations")
class ClientOrganizationController(val clientAccountService: ClientAccountService) {

    @GetMapping("/{id}")
    fun getOrganization(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                        @PathVariable id: String): ClientOrganizationResponse {

        return clientAccountService.getClientOrganizationAccount(id).toOrganization()
    }

    @PostMapping("/{id}/invitations")
    fun inviteClientUser(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                         @PathVariable id: String,
                         @Valid @RequestBody request: OrganizationUserRequest): ClientOrganizationResponse {

        clientAccountService.getClientOrganizationAccount(id).let {
            return clientAccountService.inviteOrganizationUser(clientUser, it, request.email).toOrganization()
        }
    }

    @DeleteMapping("/{id}/invitations")
    fun uninviteClientUser(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                           @PathVariable id: String,
                           @Valid @RequestBody request: OrganizationUserRequest): ClientOrganizationResponse {

        clientAccountService.getClientOrganizationAccount(id).let {
            return clientAccountService.uninviteOrganizationUser(clientUser, it, request.email).toOrganization()
        }
    }
}