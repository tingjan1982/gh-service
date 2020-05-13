package io.geekhub.service.likes.service

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.likes.data.LikableObject
import io.geekhub.service.likes.data.LikeRecord

interface LikeService {

    fun like(clientAccount: ClientAccount, likableObject: LikableObject): LikeRecord

    fun unlike(clientAccount: ClientAccount, likableObject: LikableObject)
}