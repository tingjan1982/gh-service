package io.geekhub.service.shared.extensions

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.specialization.repository.Specialization

object DummyObject {

    fun dummyClient() = ClientAccount(accountType = ClientAccount.AccountType.CORPORATE,
            clientName = "Default Client",
            email = "default@geekhub.tw"
    )

    fun dummySpecialization() = Specialization(name = "Front End Engineer")
}
