package io.geekhub.service.shared.authorization

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import javax.sql.DataSource

/**
 * Spring OAuth2 reference: https://projects.spring.io/spring-security-oauth/docs/oauth2.html
 * Example: https://github.com/spring-projects/spring-security-oauth/tree/master/samples/oauth2/sparklr/src/main/java/org/springframework/security/oauth/examples/sparklr/config
 *
 * Reference:
 * http://www.baeldung.com/spring-security-oauth-jwt
 * https://medium.com/@nydiarra/secure-a-spring-boot-rest-api-with-json-web-token-reference-to-angular-integration-e57a25806c50
 */
@Configuration
@EnableAuthorizationServer
class OAuth2AuthorizationServerConfig : AuthorizationServerConfigurerAdapter() {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(OAuth2AuthorizationServerConfig::class.java)
    }

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var dataSource: DataSource

    @Autowired
    @Qualifier("userDetailsServiceBean")
    private lateinit var userDetailsService: UserDetailsService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Throws(Exception::class)
    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints.tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter())
                .authenticationManager(this.authenticationManager)
                .userDetailsService(this.userDetailsService)
    }

    @Throws(Exception::class)
    override fun configure(clients: ClientDetailsServiceConfigurer) {

        try {
            clients.jdbc(this.dataSource)
                    .passwordEncoder(this.passwordEncoder)
                    .withClient("roclient")
                    .secret("secret")
                    .resourceIds(OAuthSettings.resourceId)
                    .authorizedGrantTypes("client_credentials")
                    .scopes("read")
                    .accessTokenValiditySeconds(3600)
                    .refreshTokenValiditySeconds(3600)
                    .and()
                    .withClient("shortlivedclient")
                    .secret("secret")
                    .resourceIds(OAuthSettings.resourceId)
                    .authorizedGrantTypes("client_credentials")
                    .scopes("read")
                    .accessTokenValiditySeconds(10)
                    .refreshTokenValiditySeconds(10)
                    .and()
                    .withClient("ghfront")
                    .secret("secret")
                    .resourceIds(OAuthSettings.resourceId)
                    .authorizedGrantTypes("password", "refresh_token")
                    .scopes("read", "write")
                    .accessTokenValiditySeconds(3600)
                    .refreshTokenValiditySeconds(3600)

        } catch (ex: ClientAlreadyExistsException) {
            logger.warn("Client details are already created, skipping creation.")
        }
    }

    @Bean
    fun tokenStore(): TokenStore {
        return JwtTokenStore(accessTokenConverter())
    }

    @Bean
    fun accessTokenConverter(): JwtAccessTokenConverter {
        val converter = JwtAccessTokenConverter()
        converter.setSigningKey(OAuthSettings.signingKey)
        return converter
    }

    @Bean
    @Primary
    fun tokenServices(): DefaultTokenServices {
        val defaultTokenServices = DefaultTokenServices()
        defaultTokenServices.setTokenStore(tokenStore())
        defaultTokenServices.setSupportRefreshToken(true)
        return defaultTokenServices
    }
}