package io.geekhub.service.questions.model

import java.util.*

/**
 * Represents the base question class.
 */
open class Question {

    lateinit var id: String

    var categories: Set<String> = setOf()

    var topics: Set<String> = setOf()

    var creationDate: Date = Date()

    var modificationDate: Date = Date()

    var modifiedBy: String = ""

    var contributedBy: String = ""



}