package io.geekhub.service.likes.data

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientUser

interface LikableObject {
    var likeCount: Long

    fun getClientAccountId(): String
    fun getObjectIdPrefix(): String
    fun getObjectId(): String
    fun getObjectType(): String

    @Deprecated("use clientUser variation instead")
    fun id(clientAccount: ClientAccount): String {
        return "${clientAccount.id}:${this.getObjectIdPrefix()}-${this.getObjectId()}"
    }

    fun id(clientUser: ClientUser): String {
        return "${clientUser.id}:${this.getObjectIdPrefix()}-${this.getObjectId()}"
    }
}