package io.geekhub.service.questions.web.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import io.geekhub.service.shared.model.PageableResponse
import org.springframework.data.domain.Page

data class QuestionsResponse(@JsonIgnore val page: Page<QuestionResponse>,
                             @JsonIgnore val contextPath: String,
                             @JsonIgnore val resourcePrefix: String) : PageableResponse<QuestionResponse>(page, contextPath, resourcePrefix)
