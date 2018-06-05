package io.geekhub.service.questions.web.bean

import javax.validation.constraints.NotEmpty

data class AnswerRequest(
        @field:NotEmpty
        val correctAnswer: String,
        var possibleAnswers: List<String>? = listOf()
)