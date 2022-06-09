package io.geekhub.service.account.repository

import org.springframework.data.mongodb.repository.MongoRepository

interface ClientUserRepository : MongoRepository<ClientUser, String> {

    fun findByEmail(email: String): ClientUser?

    fun findByClientAccountAndAccountPrivilege(clientAccount: ClientAccount, accountPrivilege: ClientUser.AccountPrivilege): ClientUser?

    fun findAllByClientAccount(clientAccount: ClientAccount): List<ClientUser>

    fun existsByDepartment(department: ClientDepartment): Boolean

    fun existsByClientAccountAndEmail(organization: ClientAccount, email: String): Boolean

    fun findFirstByNickname(name: String): ClientUser?
}