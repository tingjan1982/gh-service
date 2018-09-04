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
import org.springframework.security.oauth2.config.annotation.builders.JdbcClientDetailsServiceBuilder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import javax.sql.DataSource

/**
 * Spring OAuth2 reference: https://projects.spring.io/spring-security-oauth/docs/oauth2.html
 * Example: https://github.com/spring-projects/spring-security-oauth/tree/master/samples/oauth2/sparklr/src/main/java/org/springframework/security/oauth/examples/sparklr/config
 *
 * Reference:
 * Extra claims - http://www.baeldung.com/spring-security-oauth-jwt
 * https://medium.com/@nydiarra/secure-a-spring-boot-rest-api-with-json-web-token-reference-to-angular-integration-e57a25806c50
 */
@Configuration
@EnableAuthorizationServer
class OAuth2AuthorizationServerConfig : AuthorizationServerConfigurerAdapter() {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(OAuth2AuthorizationServerConfig::class.java)
    }

    @Autowired
    private lateinit var userIdTokenEnhancer: UserIdTokenEnhancer

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var dataSource: DataSource

    @Autowired
    @Qualifier("userDetailsServiceBean")
    private lateinit var userDetailsService: UserDetailsService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var customAccessTokenConverter: CustomAccessTokenConverter

    @Throws(Exception::class)
    override fun configure(clients: ClientDetailsServiceConfigurer) {

        val builder = TolerantClientDetailsServiceBuilder(this.dataSource, this.passwordEncoder)
                .dataSource(this.dataSource).passwordEncoder(this.passwordEncoder)
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
                .and()

        clients.setBuilder(builder)
    }

    @Throws(Exception::class)
    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {

        val tokenEnhancerChain = TokenEnhancerChain()
        tokenEnhancerChain.setTokenEnhancers(listOf(this.userIdTokenEnhancer, this.accessTokenConverter()))

        endpoints.tokenStore(tokenStore())
                .tokenEnhancer(tokenEnhancerChain)
                .authenticationManager(this.authenticationManager)
                .userDetailsService(this.userDetailsService)
    }

    @Bean
    @Primary
    fun tokenServices(): DefaultTokenServices {
        val defaultTokenServices = DefaultTokenServices()
        defaultTokenServices.setTokenStore(tokenStore())
        defaultTokenServices.setSupportRefreshToken(true)
        return defaultTokenServices
    }

    @Bean
    fun tokenStore(): TokenStore {
        return JwtTokenStore(accessTokenConverter())
    }

    @Bean
    fun accessTokenConverter(): JwtAccessTokenConverter {
        val converter = JwtAccessTokenConverter()
        converter.accessTokenConverter = this.customAccessTokenConverter
        converter.setSigningKey(OAuthSettings.signingKey)
        return converter
    }

    

    /**
     * This class was introduced to circumvent an issue in multi-node environment
     * due to the nature of lazily initialized JdbcClientDetailsService. The fact
     * that multiple nodes sharing the same data store results in one node creating
     * and persisting client details successfully, meanwhile the other node attempting to create the client details
     * is returned with a ClientAlreadyExistsException.
     */
    private class TolerantClientDetailsServiceBuilder(val dataSource: DataSource, val passwordEncoder: PasswordEncoder) : JdbcClientDetailsServiceBuilder() {

        override fun performBuild(): ClientDetailsService {
            try {
                return super.performBuild()
            } catch (ex: ClientAlreadyExistsException) {
                JdbcClientDetailsService(this.dataSource).let {
                    it.setPasswordEncoder(this.passwordEncoder)
                    return it
                }
            }
        }
    }
}