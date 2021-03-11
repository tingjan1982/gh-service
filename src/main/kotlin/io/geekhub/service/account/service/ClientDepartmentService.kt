package io.geekhub.service.account.service

import io.geekhub.service.account.repository.ClientDepartment
import io.geekhub.service.account.repository.ClientUser

interface ClientDepartmentService {

    fun createClientDepartment(user: ClientUser, departmentName: String): ClientDepartment

    fun saveClientDepartment(department: ClientDepartment): ClientDepartment

    fun getDepartment(id: String): ClientDepartment

    fun deleteClientDepartment(department: ClientDepartment)
}
