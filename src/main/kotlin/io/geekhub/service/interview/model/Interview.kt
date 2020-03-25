package io.geekhub.service.interview.model

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.questions.model.Question
import io.geekhub.service.specialization.repository.Specialization
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Interview(
        @Id
        var id: String? = null,
        var title: String,
        var description: String? = null,
        var jobTitle: String,
        @DBRef
        var clientAccount: ClientAccount,
        @DBRef
        var specialization: Specialization,
        /**
         * Left part of the pair represents the ordering key.
         */
        val sections: MutableList<Section> = mutableListOf()
) {

    fun addSection(section: Section) {
        sections.add(section)
    }

    data class Section(val title: String,
                       val questions: MutableList<QuestionSnapshot> = mutableListOf()) {

        fun addQuestion(order: String, question: Question): Section {
            questions.add(question.toQuestionSnapshot(order)).let {
                return this
            }
        }
    }

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
