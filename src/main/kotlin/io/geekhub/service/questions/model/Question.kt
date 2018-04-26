package io.geekhub.service.questions.model

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Transient

/**
 * Represents the base question class.
 */
@Entity
abstract class Question {

    @Id
    lateinit var id: String

    /**
     * The weight of the question for result calculation.
     */
    var weight: Double = 0.0

    @Transient
    var categories: Set<String> = setOf()

    @Transient
    var topics: Set<String> = setOf()

    var creationDate: Date = Date()

    var modificationDate: Date = Date()

    var modifiedBy: String? = null

    var contributedBy: String? = null



}