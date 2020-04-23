package io.geekhub.service.shared.model

import org.springframework.data.domain.Page
import org.springframework.web.util.UriComponentsBuilder

open class PageableResponse<T>(page: Page<T>, navigationLinkBuilder: UriComponentsBuilder) {

    var total: Long = page.totalElements
    var totalPages = page.totalPages
    var size: Int = page.size
    var currentPage: Int = page.number
    var results: List<T> = page.content
    var next: String = if (page.hasNext()) navigationLinkBuilder.replaceQueryParam("page", currentPage + 1).toUriString() else ""
    var prev: String = if (page.hasPrevious()) navigationLinkBuilder.replaceQueryParam("page", currentPage - 1).toUriString() else ""

}