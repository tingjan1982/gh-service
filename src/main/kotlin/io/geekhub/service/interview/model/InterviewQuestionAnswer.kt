package io.geekhub.service.interview.model

import io.geekhub.service.questions.model.Question
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "gh_interview_questions")
data class InterviewQuestionAnswer(
        @EmbeddedId
        val id: PK,
        var answer: String? = null
) {

    @Embeddable
    data class PK(@ManyToOne @JoinColumn(name = "interview_id") val interview: Interview,
                  @ManyToOne @JoinColumn(name = "question_id") val question: Question) : Serializable
}