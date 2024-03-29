package io.geekhub.service.shared.config

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientAccountRepository
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.account.repository.ClientUserRepository
import io.geekhub.service.account.service.ClientAccountService
import io.geekhub.service.account.service.ClientUserService
import io.geekhub.service.shared.extensions.DummyObject
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.annotation.PreDestroy

@Configuration
class TestConfig(
    val clientAccountService: ClientAccountService,
    val clientUserService: ClientUserService,
    val clientAccountRepository: ClientAccountRepository,
    val clientUserRepository: ClientUserRepository
) {

    val clientAccount = DummyObject.dummyClientAccount()

    var clientUser: ClientUser? = null

    @Bean
    fun defaultClientAccount(): ClientAccount {

        return clientAccount.id?.let {
            clientAccount
        } ?: clientAccountService.saveClientAccount(clientAccount)
    }

    @Bean
    fun defaultClientUser(clientAccount: ClientAccount): ClientUser {

        clientUser?.let {
            return it
        } ?: clientUserService.saveClientUser(DummyObject.dummyClientUser(clientAccount)).let {
            clientUser = it

            return it
        }
    }

    @PreDestroy
    fun removeUsers() {

        clientAccountRepository.delete(clientAccount)
        clientUserRepository.delete(clientUser!!)
    }

}