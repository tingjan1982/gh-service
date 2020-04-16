package io.geekhub.service.shared.model

import java.util.*

abstract class BaseResponse {
    abstract val deleted: Boolean
    abstract val lastModifiedDate: Date?
}
