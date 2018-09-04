package io.geekhub.service.questions.model

import javax.persistence.*

@Entity(name = "gh_question_answer")
@SequenceGenerator(name = "question_answer_sequence", sequenceName = "gh_question_answer_seq")
data class PossibleAnswer(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_answer_sequence")
        var id: Long? = null,

        @ManyToOne
        @JoinColumn(name = "question_id")
        var question: Question? = null,

        val answer: String,
        val correct: Boolean) {

    companion object {
        fun noAnswer(): PossibleAnswer {
            return PossibleAnswer(answer = "No Answer", correct = false)
        }
    }

}