package io.geekhub.service.account.repository

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class ClientAccount(
        @Id
        var id: String? = null,
        var accountType: AccountType,
        var clientName: String,
        var email: String) {

    enum class AccountType {
        CORPORATE, INDIVIDUAL
    }
}