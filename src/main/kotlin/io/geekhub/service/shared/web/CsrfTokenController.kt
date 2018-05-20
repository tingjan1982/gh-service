package io.geekhub.service.shared.web

import org.springframework.security.web.csrf.CsrfToken
import org.springframework.security.web.csrf.CsrfTokenRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

/**
 * This controller should only ever be accessed by admin user as it exposes
 * csrf token for testing purpose
 */
@RestController
@RequestMapping("/csrf-token")
class CsrfTokenController(val csrfTokenRepository: CsrfTokenRepository) {

    @GetMapping
    fun login(httpRequest: HttpServletRequest): CsrfToken {
        return csrfTokenRepository.loadToken(httpRequest)
    }
}