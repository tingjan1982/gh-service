package io.geekhub.service.shared.web.filter

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.service.ClientAccountService
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class ClientAccountFilter(val clientAccountService: ClientAccountService) : OncePerRequestFilter() {

    companion object {
        const val CLIENT_KEY = "CLIENT_KEY"
        private val logger = LoggerFactory.getLogger(ClientAccountFilter::class.java)
    }


    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {

        resolveClientAccount()?.let {
            logger.info("Resolved client account: $it")
            request.setAttribute(CLIENT_KEY, it)
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveClientAccount(): ClientAccount? {

        SecurityContextHolder.getContext().authentication.let { auth ->
            val principal = auth.principal

            if (principal is Jwt) {
                val id = principal.claims["sub"] as String

                clientAccountService.getClientAccount(id)?.let {
                    return it
                }

                val email = principal.claims["https://api.geekhub.tw/email"] as String
                ClientAccount(id, ClientAccount.AccountType.INDIVIDUAL, email, email).let {
                    return clientAccountService.saveClientAccount(it)
                }
            }
        }

        throw RuntimeException("Authentication object is not jwt")
    }
}