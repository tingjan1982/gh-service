package io.geekhub.service.specialization.repository

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Specialization(
        @Id
        var id: String? = null,
        @Indexed(unique = true)
        var name: String
)
