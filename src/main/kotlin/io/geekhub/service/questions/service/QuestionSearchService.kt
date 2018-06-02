package io.geekhub.service.questions.service

import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.web.bean.SearchRequest
import org.springframework.data.domain.Page

interface QuestionSearchService {

    fun searchQuestions(searchRequest: SearchRequest): Page<Question>
}