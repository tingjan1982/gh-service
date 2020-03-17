package io.geekhub.service.questions.model

//import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

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
         * The field of profession this question belongs to.
         */
        var category: String,

        /**
         * Specific sub-domains this question relates to.
         */
        var topic: String) {

    val possibleAnswers: MutableList<PossibleAnswer> = mutableListOf()

    val attributes: MutableMap<String, QuestionAttribute> = mutableMapOf()


    fun addAnswer(answer: PossibleAnswer) {
        this.possibleAnswers.add(answer)
    }

    fun addAttribute(attribute: QuestionAttribute) {
        attributes[attribute.key] = attribute
    }

    fun getAttribute(attributeKey: String): QuestionAttribute? {
        return attributes[attributeKey]
    }

    data class PossibleAnswer(
            val answer: String,
            val correctAnswer: Boolean
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
