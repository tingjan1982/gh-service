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
        var contributedBy: String? = null) : BaseAuditableObject<Question, String>() {


    @OneToMany(mappedBy = "question", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val possibleAnswers: MutableList<PossibleAnswer> = mutableListOf()

    @OneToMany(mappedBy = "question", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @MapKey(name = "key")
    private val attributes: MutableMap<String, QuestionAttribute> = mutableMapOf()


    fun addAnswer(answer: PossibleAnswer) {
        this.possibleAnswers.add(answer)
        answer.question = this
    }

    override fun getId(): String? {
        return this.questionId
    }

    enum class Difficulty {
        EASY, INTERMEDIATE, HARD
    }

    enum class VisibilityScope {
        PUBLIC, PRIVATE
    }
}
