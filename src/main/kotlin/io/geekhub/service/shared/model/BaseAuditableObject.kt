package io.geekhub.service.shared.model

import org.springframework.data.jpa.domain.AbstractAuditable
import java.io.Serializable

abstract class BaseAuditableObject<U, PK: Serializable> : AbstractAuditable<U, PK>()