package io.geekhub.service.interview.model

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.likes.data.LikableObject
import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.model.BaseMongoObject
import io.geekhub.service.shared.model.Visibility
import io.geekhub.service.shared.service.data.ClientUserObject
import io.geekhub.service.shared.userkey.UserKeyObject
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.TextIndexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Interview(
    @Id
    var id: String? = null,
    @TextIndexed
    var title: String,
    @TextIndexed
    var description: String? = null,
    @TextIndexed
    var jobTitle: String,
    @DBRef
    override var clientUser: ClientUser,
    val ownershipType: OwnershipType = OwnershipType.DEFAULT,
    var clientAccount: String? = null,
    var defaultDuration: Int = -1,
    var visibility: Visibility,
    var releaseResult: ReleaseResult,
    var sections: MutableList<Section> = mutableListOf(),
    var latestPublishedInterviewId: String? = null,
    override var likeCount: Long = 0,
    override var userKey: String? = null
) : BaseMongoObject(), LikableObject, ClientUserObject, UserKeyObject {

    @DBRef
    var lightInterviewSessions: MutableList<LightInterviewSession> = mutableListOf()


    fun addInterviewSession(interviewSession: LightInterviewSession) {
        lightInterviewSessions.add(interviewSession)
    }

    fun groupInterviewSessions(): Map<InterviewSession.Status, List<String>> {

        return lightInterviewSessions.groupBy({
            it.status
        }, {
            it
        }).mapValues { it -> it.value.map { it.id }.toList() }
    }

    override fun getClientUserId(): String {
        return clientUser.id.toString()
    }

    override fun getObjectIdPrefix(): String {
        return "itvw"
    }

    override fun getObjectId(): String {
        return id.toString()
    }

    override fun getObjectType(): String {
        return this::class.toString()
    }

    fun copy(clientUser: ClientUser): Interview {

        return this.copy(
            id = null,
            title = "Copy of ${this.title}",
            clientUser = clientUser,
            clientAccount = clientUser.clientAccount.id,
            sections = this.sections,
            latestPublishedInterviewId = null,
            likeCount = 0,
            userKey = null
            )
    }

    data class Section(
        val id: String = ObjectId.get().toString(),
        val title: String,
        var questions: MutableList<QuestionSnapshot> = mutableListOf()
    )

    data class QuestionSnapshot(
        val id: String = ObjectId().toString(),
        var question: String,
        val questionType: Question.QuestionType,
        val possibleAnswers: MutableList<Question.PossibleAnswer> = mutableListOf()
    )

    enum class ReleaseResult {
        YES, NO
    }

    enum class OwnershipType {

        /**
         * User's belonging client account
         */
        DEFAULT,

        /**
         * User's individual client account
         */
        PERSONAL
    }
}