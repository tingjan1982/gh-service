package io.geekhub.service.likes.data

import io.geekhub.service.account.repository.ClientAccount

interface LikableObject {
    var likeCount: Long

    fun getClientAccountId(): String
    fun getObjectIdPrefix(): String
    fun getObjectId(): String
    fun getObjectType(): String

    fun id(clientAccount: ClientAccount): String {
        return "${clientAccount.id}:${this.getObjectIdPrefix()}-${this.getObjectId()}"
    }
}