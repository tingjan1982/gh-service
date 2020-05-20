package io.geekhub.service.likes.service

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.likes.data.LikableObject
import io.geekhub.service.likes.data.LikeRecord
import kotlin.reflect.KClass

interface LikeService {

    fun like(clientAccount: ClientAccount, likableObject: LikableObject): LikeRecord

    fun unlike(clientAccount: ClientAccount, likableObject: LikableObject)

    fun getLikedObjects(clientAccount: ClientAccount, likableObjectType: KClass<out LikableObject>): List<LikeRecord>
}