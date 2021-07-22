package io.geekhub.service.likes.service

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.likes.data.LikableObject
import io.geekhub.service.likes.data.LikeRecord
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import kotlin.reflect.KClass

interface LikeService {

    fun like(clientUser: ClientUser, likableObject: LikableObject): LikeRecord

    fun unlike(clientUser: ClientUser, likableObject: LikableObject)

    fun getLikedObjects(clientUser: ClientUser, likableObjectType: KClass<out LikableObject>): List<LikeRecord>

    fun <T : LikableObject> getLikedObjectsAsType(clientUser: ClientUser, likableObjectType: KClass<T>, pageRequest: PageRequest, keyword: String? = null): Page<T>
}