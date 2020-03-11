package io.geekhub.service.questions.model

//import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.mongodb.core.mapping.Document
import javax.persistence.Id

/**
 * Represents the base question class.
 *
 * Reference on JPA hierarchical class options: https://www.thoughts-on-java.org/complete-guide-inheritance-strategies-jpa-hibernate/
 */
@Document
data class Question(
        @Id
        var questionId: String? = null,
        var question: String,

        /**
         * The weight of the question for result calculation.
         */
        var weight: Double = 1.0,
        var difficulty: Difficulty = Difficulty.INTERMEDIATE,

        /**
         * The field of profession this question belongs to.
         */
        var category: String = "",

        /**
         * Specific sub-domains this question relates to.
         */
        var topic: String = "",

        /**
         * Use this to control who sees the question.
         */
        var visibilityScope: VisibilityScope = VisibilityScope.PUBLIC,
        var contributedBy: String? = null) {

    val possibleAnswers: MutableList<PossibleAnswer> = mutableListOf()

    private val attributes: MutableMap<String, QuestionAttribute> = mutableMapOf()


    fun addAnswer(answer: PossibleAnswer) {
        this.possibleAnswers.add(answer)
    }

    fun addAttribute(attribute: QuestionAttribute) {
        attributes[attribute.key] = attribute
    }

    fun getAttribute(attributeKey: String): QuestionAttribute? {
        return attributes[attributeKey]
    }
    
    enum class Difficulty {
        EASY, INTERMEDIATE, HARD
    }

    enum class VisibilityScope {
        PUBLIC, PRIVATE
    }

    data class PossibleAnswer(
            val answer: String,
            val correct: Boolean
    )

    data class QuestionAttribute(
            val key: String,
            var value: String) {

        companion object {
            const val DESCRIPTION_KEY = "description"
            const val TOTAL_LIKES_KEY = "total_likes"
        }
    }
}
