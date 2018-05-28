package io.geekhub.service.questions.model

// TODO: work on this
data class Answer(
        val correctAnswer: String,
        val possibleAnswers: MutableList<String> = mutableListOf()) {

    companion object {
        fun noAnswer(): Answer {
            return Answer("No Answer")
        }
    }

}