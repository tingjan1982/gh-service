package io.geekhub.service.questions.web.bean

data class AnswerRequest(
        val correctAnswer: String,
        var possibleAnswers: List<String>? = listOf()
)