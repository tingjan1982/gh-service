package io.geekhub.service.questions.web.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import io.geekhub.service.shared.model.PageableResponse
import org.springframework.data.domain.Page
import org.springframework.web.util.UriComponentsBuilder

data class QuestionsResponse(@JsonIgnore val page: Page<QuestionResponse>,
                             @JsonIgnore val navigationLinkBuilder: UriComponentsBuilder) : PageableResponse<QuestionResponse>(page, navigationLinkBuilder)
