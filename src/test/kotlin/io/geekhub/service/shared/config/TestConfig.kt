package io.geekhub.service.shared.config

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.account.service.ClientAccountService
import io.geekhub.service.account.service.ClientUserService
import io.geekhub.service.shared.extensions.DummyObject
import io.geekhub.service.specialization.repository.Specialization
import io.geekhub.service.specialization.service.SpecializationService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestConfig(val clientAccountService: ClientAccountService,
                 val clientUserService: ClientUserService,
                 val specializationService: SpecializationService) {

    val clientAccount = DummyObject.dummyClient()

    var clientUser: ClientUser? = null

    val specialization = DummyObject.dummySpecialization()

    @Bean
    fun defaultClientAccount(): ClientAccount {

        return clientAccount.id?.let {
            clientAccount
        } ?: clientAccountService.saveClientAccount(clientAccount)
    }

    @Bean
    fun defaultClientUser(clientAccount: ClientAccount): ClientUser {
        return clientUser?.let {
            clientUser
        } ?: clientUserService.saveClientUser(DummyObject.dummyClientUser(clientAccount))
    }

    @Bean
    fun defaultSpecialization(): Specialization {

        return specialization.id?.let {
            specialization
        } ?: specializationService.saveSpecialization(specialization)
    }


}