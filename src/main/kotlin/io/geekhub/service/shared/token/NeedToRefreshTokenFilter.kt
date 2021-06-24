package io.geekhub.service.shared.token

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class NeedToRefreshTokenFilter : OncePerRequestFilter() {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(NeedToRefreshTokenFilter::class.java)
    }
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {

        SecurityContextHolder.getContext().authentication.let {
            if (it is JwtAuthenticationToken) {

                if (NeedToRefreshTokenHolder.containsNeedToRefreshToken(it.token.tokenValue)) {
                    val msg = "The passed in token is marked as outdated, please obtain a new token"
                    LOGGER.warn(msg)
                    response.sendError(401, msg)
                    return
                }
            } 
        }

        filterChain.doFilter(request, response)
    }
}