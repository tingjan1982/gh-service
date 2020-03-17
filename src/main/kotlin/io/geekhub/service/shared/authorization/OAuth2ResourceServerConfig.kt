package io.geekhub.service.shared.authorization

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.DefaultTokenServices


/**
 * Configuration for a Resource Server that serves protected resources provided by this application. Protection is offered
 * via OAuth2 token.
 *
 * Reference: https://projects.spring.io/spring-security-oauth/docs/oauth2.html
 */
//@Configuration
//@EnableResourceServer
class OAuth2ResourceServerConfig : ResourceServerConfigurerAdapter() {

    @Autowired
    private lateinit var tokenServices: DefaultTokenServices


    override fun configure(config: ResourceServerSecurityConfigurer) {
        config.tokenServices(tokenServices)
                .resourceId(OAuthSettings.resourceId)
                .stateless(false)
    }

    /**
     * Defines authorization on protected resources.
     *
     * Stateless is more secure as it doesn't authenticate based on existing http session
     * that could reside in client side.
     *
     * Reference: OAuth2SecurityExpressionMethods class contains all supported SpEL for providing expression based oauth2 access control.
     */
    override fun configure(http: HttpSecurity) {

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors()
                .and().authorizeRequests()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/v2/api-docs", "/swagger-ui.html", "/webjars/**", "/swagger-resources/**").permitAll()
                .antMatchers(HttpMethod.GET, "/questions", "/questions/**").permitAll()
                .antMatchers(HttpMethod.POST, "/questions", "/questions/**").access("#oauth2.hasScope('write') and hasAnyRole('USER', 'ADMIN')")
                .antMatchers(HttpMethod.GET, "/users/**").access("#oauth2.hasScope('read')")
                .antMatchers(HttpMethod.POST, "/users/**").access("#oauth2.hasScope('write') and hasAnyRole('USER', 'ADMIN')")
                .anyRequest().authenticated()
    }
}