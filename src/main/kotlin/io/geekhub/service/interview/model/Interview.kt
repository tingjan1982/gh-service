package io.geekhub.service.interview.model

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.likes.data.LikableObject
import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.model.BaseMongoObject
import io.geekhub.service.shared.model.Visibility
import io.geekhub.service.shared.service.data.ClientUserObject
import io.geekhub.service.shared.userkey.UserKeyObject
import io.geekhub.service.specialization.repository.Specialization
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
    @DBRef
    var specialization: Specialization?,
    var defaultDuration: Int = -1,
    var visibility: Visibility,
    var releaseResult: ReleaseResult,
    var sections: MutableList<Section> = mutableListOf(),
    var latestPublishedInterviewId: String? = null,
    override var likeCount: Long = 0,
    override var userKey: String? = null,
    @DBRef(lazy = true)
    val interviewSessions: MutableList<InterviewSession> = mutableListOf()
) : BaseMongoObject(), LikableObject, ClientUserObject, UserKeyObject {

    fun addInterviewSession(interviewSession: InterviewSession) {
        interviewSessions.add(interviewSession)
    }

    fun groupInterviewSessions(): Map<InterviewSession.Status, List<String>> {

        return interviewSessions.groupBy({
            it.status
        }, {
            it
        }).mapValues { it -> it.value.map { it.id.toString() }.toList() }
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

    data class Section(val id: String = ObjectId.get().toString(),
                       val title: String,
                       var questions: MutableList<QuestionSnapshot> = mutableListOf())

    data class QuestionSnapshot(val id: String = ObjectId().toString(),
                                val question: String,
                                val questionType: Question.QuestionType,
                                val possibleAnswers: List<Question.PossibleAnswer> = listOf()
    )

    enum class ReleaseResult {
        YES, NO
    }
}