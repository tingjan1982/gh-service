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
        var category: String,

        /**
         * Specific sub-domains this question relates to.
         */
        var topic: String,

        /**
         * Use this to control whether changes will be made public.
         */
        var status: QuestionStatus = QuestionStatus.DRAFT,

        /**
         * Use this to control who sees the question.
         */
        var visibilityScope: VisibilityScope = VisibilityScope.PUBLIC,
        var contributedBy: String? = null) : BaseAuditableObject<Question, String>() {

    constructor(question: String) : this(question = question, category = "", topic = "")

    companion object {
        const val ANSWER = "ANSWER"
        const val POSSIBLE_PREFIX = "POSSIBLE_ANSWER_"
        const val POSSIBLE_TOTAL = "POSSIBLE_TOTAL"
    }

    @OneToMany(mappedBy = "question", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @MapKey(name = "key")
    private val attributes: MutableMap<String, QuestionAttribute> = mutableMapOf()

    fun addAnswer(answer: Answer) {
        val questionAttribute = QuestionAttribute(question = this, key = Question.ANSWER, value = answer.correctAnswer)
        this.attributes[ANSWER] = questionAttribute

        answer.possibleAnswers.forEachIndexed({ idx, ans ->
            val possibleAnswerKey = Question.POSSIBLE_PREFIX + idx
            QuestionAttribute(question = this, key = possibleAnswerKey, value = ans).let {
                this.attributes[possibleAnswerKey] = it
            }
        })

        this.attributes[POSSIBLE_TOTAL] = QuestionAttribute(question = this, key = POSSIBLE_TOTAL, value = answer.possibleAnswers.size.toString())
    }

    fun getAnswer(): String? {
        return this.attributes[ANSWER]?.value
    }

    fun getAnswerDetails(): Answer {

        this.getAnswer()?.let {
            val answer = Answer(it)
            val idx = this.attributes[POSSIBLE_TOTAL]?.value?.toInt() ?: 0
            for (i in 0 until idx) {
                this.attributes["$POSSIBLE_PREFIX$i"]?.let {
                    answer.possibleAnswers.add(it.value)
                }
            }

            return answer

        } ?: return Answer.noAnswer()
    }

    override fun getId(): String? {
        return this.questionId
    }

    enum class Difficulty {
        EASY, INTERMEDIATE, HARD
    }

    enum class QuestionStatus {
        DRAFT, PUBLISHED
    }

    enum class VisibilityScope {
        PUBLIC, PRIVATE
    }
}
