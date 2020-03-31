package io.geekhub.service.shared.web.filter

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.service.ClientAccountService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class ClientAccountFilter(val clientAccountService: ClientAccountService) : OncePerRequestFilter() {

    val restTemplate: RestTemplate = RestTemplate()
    /*get() {
        val httpClient: CloseableHttpClient = HttpClients.custom()
                .setSSLHostnameVerifier(NoopHostnameVerifier())
                .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                .build()
        val requestFactory = HttpComponentsClientHttpRequestFactory()
        requestFactory.httpClient = httpClient

        return RestTemplate(requestFactory)
    }*/

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
                } ?: return syncClientAccountInfo(principal)
            }
        }

        throw RuntimeException("Authentication object is not jwt")
    }

    private fun syncClientAccountInfo(jwt: Jwt): ClientAccount {

        val id = jwt.claims["sub"] as String
        val email = jwt.claims["https://api.geekhub.tw/email"] as String

        ClientAccount(id, ClientAccount.AccountType.INDIVIDUAL, email, email).let {

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            headers.setBearerAuth(jwt.tokenValue)
            val requestEntity = HttpEntity<Void>(headers)

            restTemplate.exchange("https://geekhub.auth0.com/userinfo", HttpMethod.GET, requestEntity, Auth0UserInfo::class.java).let { response ->
                response.body?.let { userInfo ->
                    println(userInfo)
                    it.avatar = userInfo.picture

                    userInfo.nickname?.let { name ->
                        it.clientName = name
                    }
                }
            }

            return clientAccountService.saveClientAccount(it)
        }
    }

    data class Auth0UserInfo(
            val nickname: String?,
            val picture: String?
    )
}