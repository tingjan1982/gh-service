package io.geekhub.service.shared.model

import io.geekhub.service.account.repository.ClientAccount
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.Temporal
import javax.persistence.TemporalType

abstract class BaseMongoObject {

    @CreatedBy
    private lateinit var createdBy: ClientAccount

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private lateinit var createdDate: LocalDateTime

    @LastModifiedBy
    private lateinit var lastModifiedBy: ClientAccount

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private lateinit var lastModifiedDate: LocalDateTime

}