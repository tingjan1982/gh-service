package io.geekhub.service.shared.extensions

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.specialization.repository.Specialization

object DummyObject {

    fun dummyClient() = ClientAccount(accountType = ClientAccount.AccountType.CORPORATE,
            clientName = "Test Client Account",
            email = "joelin@geekhub.tw"
    )

    fun dummySpecialization() = Specialization(name = "Front End Engineer")
}
