package io.geekhub.service.account.service

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientDepartment
import io.geekhub.service.account.repository.ClientDepartmentRepository
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.exception.BusinessObjectNotFoundException
import org.springframework.stereotype.Service

@Service
class ClientDepartmentServiceImpl(val repository: ClientDepartmentRepository, val clientUserService: ClientUserService) : ClientDepartmentService {

    override fun createClientDepartment(user: ClientUser, departmentName: String): ClientDepartment {

        if (!user.isCorporateAccount()) {
            throw BusinessException("Only corporate account can have department")
        }

        user.clientAccount.let { acc ->
            ClientDepartment(name = departmentName, clientAccount = acc).let {
                return repository.save(it)
            }
        }
    }

    override fun getDepartments(clientAccount: ClientAccount): List<ClientDepartment> {
        return repository.findAllByClientAccount(clientAccount)
    }

    override fun saveClientDepartment(department: ClientDepartment): ClientDepartment {
        return repository.save(department)
    }

    override fun getDepartment(id: String): ClientDepartment {

        return repository.findById(id).orElseThrow {
            throw BusinessObjectNotFoundException(ClientDepartment::class, id)
        }
    }

    override fun deleteClientDepartment(department: ClientDepartment) {

        if (clientUserService.clientUsersExistInDepartment(department)) {
            throw BusinessException("Users exist in this department, cannot delete: ${department.name}")
        }

        repository.delete(department)
    }

    override fun deleteClientAccountDepartments(clientAccount: ClientAccount) {

        repository.deleteAllByClientAccount(clientAccount)
    }
}