package io.geekhub.service.shared.web.filter

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Setting this filter to the highest precedence is to ensure that all log messages of a given request
 * is tagged with request context id.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RequestContextGenerationFilter : OncePerRequestFilter() {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(RequestContextGenerationFilter::class.java)
    }

    private val requestContextIdKey: String = "requestContextIdKey"

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {

        try {
            val requestContextId = "[${UUID.randomUUID()}] "
            MDC.put(requestContextIdKey, requestContextId)
            logger.info("Begin-Request - ${requestInfo(request)}")
            response.addHeader("X-REQUEST-CONTEXT-ID", requestContextId)
            filterChain.doFilter(request, response)
        } finally {
            logger.info("End-Request")
            MDC.clear()
        }
    }

    private fun requestInfo(request: HttpServletRequest): String {
        return "Host: ${request.remoteHost} URI: ${request.requestURI} User-Agent: ${request.getHeader("User-Agent")} X-Forwarded-For: ${request.getHeader("X-Forwarded-For")}"
    }
}