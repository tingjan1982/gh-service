package io.geekhub.service.account.repository

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Represents an account within the system. Account differentiates from User in the sense that
 * it tracks account related information such as account type, which can used for reporting,
 * plan type, which indicates the plan that the user is on.
 */
@Document
data class ClientAccount(
        @Id
        var id: String? = null,
        var accountType: AccountType,
        var planType: PlanType,
        var clientName: String // e.g. corporate name
) {

    enum class AccountType {
        CORPORATE, INDIVIDUAL
    }

    enum class PlanType {
        FREE, PREMIUM
    }
}