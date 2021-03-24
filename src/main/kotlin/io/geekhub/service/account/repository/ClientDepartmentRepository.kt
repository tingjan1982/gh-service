package io.geekhub.service.account.repository

import org.springframework.data.mongodb.repository.MongoRepository

interface ClientDepartmentRepository : MongoRepository<ClientDepartment, String> {

    fun findAllByClientAccount(clientAccount: ClientAccount): List<ClientDepartment>
}