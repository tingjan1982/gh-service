package io.geekhub.service.likes.data

import io.geekhub.service.account.repository.ClientUser

interface LikableObject {
    var likeCount: Long

    fun getClientUserId(): String
    fun getObjectIdPrefix(): String
    fun getObjectId(): String
    fun getObjectType(): String

    fun id(clientUser: ClientUser): String {
        return "${clientUser.id}:${this.getObjectIdPrefix()}-${this.getObjectId()}"
    }
}