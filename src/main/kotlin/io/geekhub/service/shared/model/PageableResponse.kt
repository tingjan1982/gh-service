package io.geekhub.service.shared.model

import org.springframework.data.domain.Page

open class PageableResponse<T>(page: Page<T>, contextPath: String, resourcePrefix: String) {

    var total: Long = page.totalElements
    var totalPages = page.totalPages
    var size: Int = page.size
    var currentPage: Int = page.number
    var results: List<T> = page.content
    var next: String = if (page.hasNext()) "${contextPath}/${resourcePrefix}?page=${currentPage + 1}&pageSize=${size}" else ""
    var prev: String = if (page.hasPrevious()) "${contextPath}/${resourcePrefix}?page=${currentPage - 1}&pageSize=${size}" else ""

}