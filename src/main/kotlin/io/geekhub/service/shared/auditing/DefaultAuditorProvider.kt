package io.geekhub.service.shared.auditing

import io.geekhub.service.account.repository.ClientAccount
import org.springframework.data.domain.AuditorAware
import java.util.*

class DefaultAuditorProvider : AuditorAware<ClientAccount> {

    override fun getCurrentAuditor(): Optional<ClientAccount> {

        return Optional.empty()
    }
}