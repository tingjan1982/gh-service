package io.geekhub.service.questions.model

//import org.springframework.data.jpa.domain.support.AuditingEntityListener
import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.shared.model.BaseMongoObject
import io.geekhub.service.specialization.repository.Specialization
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Question(
        @Id
        var questionId: String? = null,
        var question: String,
        var questionType: QuestionType = QuestionType.SHORT_ANSWER,
        var jobTitle: String?,
        @DBRef
        var clientAccount: ClientAccount,
        @DBRef
        var specialization: Specialization? = null,
        val possibleAnswers: MutableList<PossibleAnswer> = mutableListOf(),
        val attributes: MutableMap<String, QuestionAttribute> = mutableMapOf()) : BaseMongoObject() {


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

    enum class QuestionType {
        TRUE_FALSE, MULTI_CHOICE, MULTI_ANSWER, SHORT_ANSWER
    }
}
