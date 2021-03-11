package io.geekhub.service.account.web

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.account.service.ClientDepartmentService
import io.geekhub.service.account.web.model.ClientDepartmentRequest
import io.geekhub.service.account.web.model.ClientDepartmentResponse
import io.geekhub.service.shared.web.filter.ClientAccountFilter
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/departments")
class ClientDepartmentController(val clientDepartmentService: ClientDepartmentService) {

    @PostMapping
    fun createDepartment(@RequestAttribute(ClientAccountFilter.CLIENT_USER_KEY) clientUser: ClientUser,
                         @Valid @RequestBody request: ClientDepartmentRequest): ClientDepartmentResponse {

        return clientDepartmentService.createClientDepartment(clientUser, request.departmentName).toDTO()
    }

    @GetMapping("/{id}")
    fun getDepartment(@RequestAttribute(ClientAccountFilter.CLIENT_USER_KEY) clientUser: ClientUser,
                      @PathVariable id: String): ClientDepartmentResponse {

        return clientDepartmentService.getDepartment(id).toDTO()
    }

    @PostMapping("/{id}")
    fun updateDepartment(@RequestAttribute(ClientAccountFilter.CLIENT_USER_KEY) clientUser: ClientUser,
                         @PathVariable id: String,
                         @Valid @RequestBody request: ClientDepartmentRequest): ClientDepartmentResponse {

        clientDepartmentService.getDepartment(id).let {
            it.name = request.departmentName
            return clientDepartmentService.saveClientDepartment(it).toDTO()
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteDepartment(@RequestAttribute(ClientAccountFilter.CLIENT_USER_KEY) clientUser: ClientUser,
                         @PathVariable id: String) {

        clientDepartmentService.getDepartment(id).let {
            clientDepartmentService.deleteClientDepartment(it)
        }
    }
}