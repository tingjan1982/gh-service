package io.geekhub.service.questions.model

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.likes.data.LikableObject
import io.geekhub.service.shared.model.BaseMongoObject
import io.geekhub.service.shared.model.Visibility
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.TextIndexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Question(
        @Id
        var id: String? = null,
        @TextIndexed
        var question: String,
        var questionType: QuestionType,
        @TextIndexed
        var jobTitle: String?,
        @DBRef
        var clientUser: ClientUser,
        var visibility: Visibility = Visibility.PUBLIC,
        var possibleAnswers: MutableList<PossibleAnswer> = mutableListOf(),
        val attributes: MutableMap<String, QuestionAttribute> = mutableMapOf(),
        override var likeCount: Long = 0) : BaseMongoObject(), LikableObject {

    fun addAnswer(answer: PossibleAnswer) {
        this.possibleAnswers.add(answer)
    }

    fun addAttribute(attribute: QuestionAttribute) {
        attributes[attribute.key] = attribute
    }

    fun getAttribute(attributeKey: String): QuestionAttribute? {
        return attributes[attributeKey]
    }

    override fun getClientUserId(): String {
        return clientUser.id.toString()
    }

    override fun getObjectIdPrefix(): String {
        return "qstn"
    }

    override fun getObjectId(): String {
        return id.toString()
    }

    override fun getObjectType(): String {
        return this::class.toString()
    }

    data class PossibleAnswer(
            val answerId: String = ObjectId.get().toString(),
            var answer: String,
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
        MULTI_CHOICE, SHORT_ANSWER, CODING
    }
}
