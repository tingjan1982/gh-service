package io.geekhub.service.shared.extensions

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.specialization.repository.Specialization

object DummyObject {

    fun dummyClient() = ClientAccount(accountType = ClientAccount.AccountType.CORPORATE,
            clientName = "Default",
            email = "default@email.co"
    )

    fun dummySpecialization() = Specialization(name = "Front End Engineer", profession = "IT")
}
