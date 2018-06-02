package io.geekhub.service.questions.web.bean

import org.springframework.data.domain.Pageable

data class SearchRequest(
        val searchText: String,
        val category: String = "",
        val topic: String = "",
        val page: Pageable
)