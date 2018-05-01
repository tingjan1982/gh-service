package io.geekhub.service.questions.model

import java.util.*
import javax.persistence.*

/**
 * Represents the base question class.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class Question<T> {

    @Id
    lateinit var id: String

    /**
     * The weight of the question for result calculation.
     */
    var weight: Double = 1.0

    var difficulty: Difficulty? = null

    /**
     * The field of profession this question belongs to.
     */
    @Transient
    var categories: Set<String> = setOf()

    /**
     * Specific sub-domains this question relates to.
     */
    @Transient
    var topics: Set<String> = setOf()

    var creationDate: Date = Date()

    var modificationDate: Date = Date()

    var modifiedBy: String? = null

    var contributedBy: String? = null

    var answer: T? = null

    fun isCorrectAnswer(answer: Answer<T>): Boolean {
        return this.answer == answer.getAnswer()
    }

    enum class Difficulty {
        EASY, INTERMEDIATE, HARD
    }
}