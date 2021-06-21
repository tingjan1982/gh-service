package io.geekhub.service.shared.userkey

import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class UserKeyFilter : OncePerRequestFilter() {

    companion object {
        const val USER_KEY = "x-user-key"
    }

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {

        request.getHeader(USER_KEY).let {
            UserKeyHolder.setUserKey(it)
        }

        filterChain.doFilter(request, response)
    }

    object UserKeyHolder {

        var userKey: ThreadLocal<String?> = ThreadLocal()

        fun getUserKey(): String? {
            return userKey.get()
        }

        fun setUserKey(userKey: String?) {
            UserKeyHolder.userKey.set(userKey)
        }
    }
}