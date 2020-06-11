package io.geekhub.service.likes.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class LikeRecord(
        @Id
        var id: String,
        @Deprecated("use likedClientUserId instead")
        val likedClientAccount: String,
        var likedClientUserId: String? = null,
        val objectId: String,
        val objectType: String)