package io.geekhub.service.shared.auditing

import io.geekhub.service.user.model.User
import org.springframework.data.domain.AuditorAware
import java.util.*

class DefaultAuditorProvider : AuditorAware<User> {

    override fun getCurrentAuditor(): Optional<User> {

        return Optional.empty()
    }
}