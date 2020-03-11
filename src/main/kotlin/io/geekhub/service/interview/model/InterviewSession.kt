package io.geekhub.service.interview.model

import io.geekhub.service.questions.model.Question
import io.geekhub.service.user.model.User
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class InterviewSession(
        @Id
        val id: String,

        @DBRef
        val interview: Interview,
        @DBRef
        var user: User,
        val interviewMode: InterviewMode,
        val duration: Int = -1
) {
    var answers: MutableList<InterviewQuestionAnswer> = mutableListOf()

    fun addInterviewQuestionAnswer(question: Question, answer: String) {
        answers.add(InterviewQuestionAnswer(question, answer))
    }

    data class InterviewQuestionAnswer(
            val question: Question,
            val answer: String
    ) {

    }

    enum class InterviewMode {
        MOCK, REAL
    }
}