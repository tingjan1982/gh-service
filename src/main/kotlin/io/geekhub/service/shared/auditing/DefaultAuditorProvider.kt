package io.geekhub.service.shared.auditing

import io.geekhub.service.shared.extensions.currentClient
import org.springframework.data.domain.AuditorAware
import java.util.*

class DefaultAuditorProvider : AuditorAware<String> {

    override fun getCurrentAuditor(): Optional<String> {

        return currentClient()?.let {
            Optional.of(it)
        } ?: Optional.empty()
    }
}