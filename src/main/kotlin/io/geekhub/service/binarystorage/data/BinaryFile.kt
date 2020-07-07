package io.geekhub.service.binarystorage.data

import org.bson.types.Binary
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class BinaryFile(
        @Id
        val id: String? = null,
        var title: String,
        var binary: Binary)