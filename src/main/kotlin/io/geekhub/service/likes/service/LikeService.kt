package io.geekhub.service.likes.service

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.likes.data.LikableObject
import io.geekhub.service.likes.data.LikeRecord
import kotlin.reflect.KClass

interface LikeService {

    fun like(clientUser: ClientUser, likableObject: LikableObject): LikeRecord

    fun unlike(clientUser: ClientUser, likableObject: LikableObject)

    fun getLikedObjects(clientUser: ClientUser, likableObjectType: KClass<out LikableObject>): List<LikeRecord>
}