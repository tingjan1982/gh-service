package io.geekhub.service.shared.authorization

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore


@Configuration
@EnableResourceServer
class OAuth2ResourceServerConfig : ResourceServerConfigurerAdapter() {

    @Autowired
    private lateinit var customAccessTokenConverter: CustomAccessTokenConverter


    override fun configure(config: ResourceServerSecurityConfigurer) {
        config.tokenServices(tokenServices())
                .resourceId(OAuthSettings.resourceId)
                .stateless(false)
    }

    /**
     * Stateless is more secure as it doesn't authenticate based on existing http session
     * that could reside in client side.
     */
    override fun configure(http: HttpSecurity) {

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors()
                .and().authorizeRequests()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers(HttpMethod.GET, "/questions", "/questions/**").permitAll()
                .antMatchers(HttpMethod.POST, "/questions", "/questions/**").access("#oauth2.hasScope('write') and hasAnyRole('USER', 'ADMIN')")
                .antMatchers(HttpMethod.GET, "/users/**").access("#oauth2.hasScope('read')")
                .antMatchers(HttpMethod.POST, "/users/**").access("#oauth2.hasScope('write') and hasAnyRole('USER', 'ADMIN')")

                .anyRequest().authenticated()
    }

    @Bean
    @Primary
    fun tokenServices(): DefaultTokenServices {
        val defaultTokenServices = DefaultTokenServices()
        defaultTokenServices.setTokenStore(tokenStore())
        return defaultTokenServices
    }

    @Bean
    fun tokenStore(): TokenStore {
        return JwtTokenStore(accessTokenConverter())
    }

    /**
     * todo: change from symmetric key to asymmetric key signing.
     */
    @Bean
    fun accessTokenConverter(): JwtAccessTokenConverter {
        val converter = JwtAccessTokenConverter()
        converter.accessTokenConverter = this.customAccessTokenConverter
        converter.setSigningKey(OAuthSettings.signingKey)
        return converter
    }
}