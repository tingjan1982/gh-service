package io.geekhub.service.questions.model

import io.geekhub.service.shared.model.BaseAuditableObject
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*

/**
 * Represents the base question class.
 *
 * Reference on JPA hierarchical class options: https://www.thoughts-on-java.org/complete-guide-inheritance-strategies-jpa-hibernate/
 */
@Entity(name = "gh_question")
@EntityListeners(AuditingEntityListener::class)
data class Question(
        @Id
        @GeneratedValue(generator = "uuid")
        @GenericGenerator(name = "uuid", strategy = "uuid2")
        var questionId: String? = null,
        var question: String,

        /**
         * The weight of the question for result calculation.
         */
        var weight: Double = 1.0,
        var difficulty: Difficulty,

        /**
         * The field of profession this question belongs to.
         */
        var category: String,

        /**
         * Specific sub-domains this question relates to.
         */
        var topic: String,
        var contributedBy: String? = null) : BaseAuditableObject<Question, String>() {

    // TODO: remove this after refactoring
    constructor() : this(question = "", difficulty = Difficulty.INTERMEDIATE, category = "", topic = "")

    constructor(question: String) : this(question = question, difficulty = Difficulty.INTERMEDIATE, category = "", topic = "")

    companion object {
        const val ANSWER = "ANSWER"
    }

    @OneToMany(mappedBy = "question", fetch = FetchType.EAGER)
    @MapKey(name = "key")
    val attributes: MutableMap<String, QuestionAttribute> = mutableMapOf()

    fun getAnswer(): String? {
        return this.attributes[ANSWER]?.value
    }

    override fun getId(): String? {
        return this.questionId
    }

    enum class Difficulty {
        EASY, INTERMEDIATE, HARD
    }
}