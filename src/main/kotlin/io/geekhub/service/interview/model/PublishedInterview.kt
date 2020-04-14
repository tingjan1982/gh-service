package io.geekhub.service.interview.model

import io.geekhub.service.shared.model.BaseMongoObject
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class PublishedInterview(
        @Id var id: String? = null,
        val referencedInterview: Interview) : BaseMongoObject()
