package io.geekhub.service.account.web.model

import io.geekhub.service.account.repository.ClientUser

data class LightClientUserResponse(
    val id: String,
    val name: String,
    val email: String,
    val department: ClientDepartmentResponse?,
    val accountPrivilege: ClientUser.AccountPrivilege) {
}