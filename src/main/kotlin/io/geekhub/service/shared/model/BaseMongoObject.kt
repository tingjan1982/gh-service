package io.geekhub.service.shared.model

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.util.*
import javax.persistence.Temporal
import javax.persistence.TemporalType

abstract class BaseMongoObject {

    @CreatedBy
    var createdBy: String? = null

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    var createdDate: Date? = null

    @LastModifiedBy
    var lastModifiedBy: String? = null

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    var lastModifiedDate: Date? = null

}