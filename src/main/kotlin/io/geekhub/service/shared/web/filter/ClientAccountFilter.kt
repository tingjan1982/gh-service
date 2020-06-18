package io.geekhub.service.shared.web.filter

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.account.service.ClientAccountService
import io.geekhub.service.account.service.ClientUserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class ClientAccountFilter(val clientAccountService: ClientAccountService, val clientUserService: ClientUserService) : OncePerRequestFilter() {

    companion object {
        const val CLIENT_USER_KEY = "CLIENT_USER_KEY"

        private val GUEST_CLIENT_ACCOUNT = ClientAccount("guest",
                ClientAccount.AccountType.INDIVIDUAL,
                ClientAccount.PlanType.FREE,
                "guest",
                "guest@geekhub.tw")

        private val GUEST_CLIENT_USER = ClientUser(id = "guest",
                email = "guest@geekhub.tw",
                name = "guest",
                nickname = "guest",
                userType = ClientUser.UserType.AUTH0,
                clientAccount = GUEST_CLIENT_ACCOUNT)

        private val LOGGER: Logger = LoggerFactory.getLogger(ClientAccountFilter::class.java)
    }

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {

        resolveClientUser().let {
            LOGGER.info("Resolved client user: $it")
            request.setAttribute(CLIENT_USER_KEY, it)
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveClientUser(): ClientUser {

        SecurityContextHolder.getContext().authentication.let { auth ->
            val principal = auth.principal

            if (principal is Jwt) {
                val id = principal.claims["sub"] as String
                clientUserService.lookupClientUser(id)?.let {
                    return it
                } ?: return syncClientUserInfo(principal)
            }
        }

        return GUEST_CLIENT_USER
    }

    private fun syncClientUserInfo(jwt: Jwt): ClientUser {

        val id = jwt.claims["sub"] as String
        val email = jwt.claims["https://api.geekhub.tw/email"] as String

        clientUserService.getAuth0UserInfo(jwt.tokenValue).let {
            ClientAccount(id, ClientAccount.AccountType.INDIVIDUAL, ClientAccount.PlanType.FREE, it.name, it.email, it.picture).let { account ->
                clientAccountService.saveClientAccount(account)

                ClientUser(id, it.email, it.name, it.nickname, it.picture, it.getUserType(), account).let { user ->
                    return clientUserService.saveClientUser(user)
                }
            }
        }
    }
}