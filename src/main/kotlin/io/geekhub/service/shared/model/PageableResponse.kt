package io.geekhub.service.shared.model

import org.springframework.data.domain.Page

open class PageableResponse<T>(page: Page<T>, contextPath: String, resourcePrefix: String) {

    var total: Long = page.totalElements
    var totalPages = page.totalPages
    var size: Int = page.size
    var currentPage: Int = page.number
    var results: List<T> = page.content
    var next: String = "${contextPath}/${resourcePrefix}/page=${if (page.hasNext()) currentPage + 1 else -1}&pageSize=${size}"
    var prev: String = "${contextPath}/${resourcePrefix}/page=${if (page.hasPrevious()) currentPage - 1 else -1}&pageSize=${size}"

}