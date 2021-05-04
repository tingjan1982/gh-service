package io.geekhub.service.shared.model

import java.util.*

abstract class BaseResponse {
    abstract val deleted: Boolean
    abstract val createdDate: Date?
    abstract val lastModifiedDate: Date?
}
