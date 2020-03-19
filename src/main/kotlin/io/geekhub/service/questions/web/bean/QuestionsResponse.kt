package io.geekhub.service.questions.web.bean

import io.geekhub.service.questions.model.Question

data class QuestionsResponse(
        val size: Int,
        val page: Int,
        val results: List<Question> = mutableListOf()
) {


}
