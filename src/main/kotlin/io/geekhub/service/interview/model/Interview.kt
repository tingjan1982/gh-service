package io.geekhub.service.interview.model

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.model.BaseMongoObject
import io.geekhub.service.specialization.repository.Specialization
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
        var clientAccount: ClientAccount,
        @DBRef
        var specialization: Specialization,
        /**
         * Left part of the pair represents the ordering key.
         */
        var sections: MutableList<Section> = mutableListOf()
) : BaseMongoObject() {

    data class Section(val title: String,
                       var questions: MutableList<QuestionSnapshot> = mutableListOf())

    data class QuestionSnapshot(val id: String,
                                val question: String,
                                val questionType: Question.QuestionType,
                                val possibleAnswers: List<Question.PossibleAnswer>,
                                val order: String = "0"
    )
}

fun Question.toQuestionSnapshot(order: String = "0") = Interview.QuestionSnapshot(
        id = this.questionId.toString(),
        question = this.question,
        questionType = this.questionType,
        possibleAnswers = this.possibleAnswers,
        order = order
)
