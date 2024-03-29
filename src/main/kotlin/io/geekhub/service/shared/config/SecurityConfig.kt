package io.geekhub.service.shared.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import java.util.List

/**
 * Spring + Auth0 integration guide:
 * https://auth0.com/blog/spring-boot-authorization-tutorial-secure-an-api-java/
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    prePostEnabled = true,
    securedEnabled = true,
    jsr250Enabled = true
)
class SecurityConfig : WebSecurityConfigurerAdapter() {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(SecurityConfig::class.java)
    }

    @Value("\${auth0.audience}")
    lateinit var audience: String

    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    lateinit var issuer: String

    /**
     * The URL patterns do not need to include the context path.
     *
     * CSRF reference:
     * https://docs.spring.io/spring-security/site/docs/5.0.5.RELEASE/reference/htmlsingle/#csrf
     * https://security.stackexchange.com/questions/166724/should-i-use-csrf-protection-on-rest-api-endpoints
     */
    override fun configure(http: HttpSecurity) {

        http.csrf().disable().cors()

        http.authorizeRequests()
            .mvcMatchers(HttpMethod.GET, "/users/**/avatar", "/users/*").permitAll()
            .mvcMatchers(HttpMethod.GET, "/users/**").authenticated()
            .mvcMatchers(HttpMethod.POST, "/users/**").not().hasAuthority("SCOPE_Guest")
            .mvcMatchers(HttpMethod.DELETE, "/users/**").not().hasAuthority("SCOPE_Guest")
            .mvcMatchers(HttpMethod.GET, "/organizations/**").authenticated()
            .mvcMatchers(HttpMethod.POST, "/organizations/**").not().hasAuthority("SCOPE_Guest")
            .mvcMatchers(HttpMethod.DELETE, "/organizations/**").not().hasAuthority("SCOPE_Guest")
            .mvcMatchers(HttpMethod.GET, "/departments/**").authenticated()
            .mvcMatchers(HttpMethod.POST, "/departments/**").not().hasAuthority("SCOPE_Guest")
            .mvcMatchers(HttpMethod.DELETE, "/departments/**").not().hasAuthority("SCOPE_Guest")
            .mvcMatchers(HttpMethod.POST, "/questions/**").authenticated()
            .mvcMatchers(HttpMethod.DELETE, "/questions/**").authenticated()
            .mvcMatchers(HttpMethod.POST, "/interviews/**").authenticated()
            .mvcMatchers(HttpMethod.DELETE, "/interviews/**").authenticated()
            .mvcMatchers(HttpMethod.POST, "/interviewSessions/**").authenticated()
            .mvcMatchers("/**").permitAll()
            .and()
            .oauth2ResourceServer().jwt {
                it.jwtAuthenticationConverter(jwtAuthenticationConverter())
            }

    }

    /**
     * Introspect and extract permissions and roles from JWT token claims
     * into a list of GrantedAuthorities for authorization later.
     */
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val permissionsConverter = JwtGrantedAuthoritiesConverter()
        permissionsConverter.setAuthoritiesClaimName("permissions")

        val rolesConverter = JwtGrantedAuthoritiesConverter()
        rolesConverter.setAuthoritiesClaimName("https://api.geekhub.tw/roles")

        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(CompositeGrantedAuthoritiesConverter(listOf(permissionsConverter, rolesConverter)))

        return jwtAuthenticationConverter
    }

    @Bean
    fun jwtDecoder(builder: RestTemplateBuilder): JwtDecoder? {

//        val rest: RestOperations = builder
//            .setConnectTimeout(Duration.ofSeconds(2))
//            .setReadTimeout(Duration.ofSeconds(2))
//            .build()
//        val jwtDecoder = NimbusJwtDecoder.withJwkSetUri(issuer).restOperations(rest).build()

        val jwtDecoder: NimbusJwtDecoder = JwtDecoders
            .fromOidcIssuerLocation(issuer) as NimbusJwtDecoder

        val audienceValidator: OAuth2TokenValidator<Jwt> = AudienceValidator(audience)
        val withIssuer: OAuth2TokenValidator<Jwt> = JwtValidators.createDefaultWithIssuer(issuer)
        val withAudience: OAuth2TokenValidator<Jwt> = DelegatingOAuth2TokenValidator(withIssuer, audienceValidator)
        jwtDecoder.setJwtValidator(withAudience)
        return jwtDecoder
    }

    /**
     * This is the only way to make CORS work with Spring Security OAuth2 because the default
     * configuration (tracked through @EnableAuthorizationServer @Import) does not enable CORS
     * hence the built security filter chain does not include CorsFilter.
     *
     * Solution found here:
     * https://github.com/spring-projects/spring-security-oauth/issues/938
     *
     * CORS reference:
     * https://docs.spring.io/spring/docs/5.0.5.RELEASE/spring-framework-reference/web.html#mvc-cors
     */
    @Bean
    fun myCorsFilter(): FilterRegistrationBean<CorsFilter> {
        val source = UrlBasedCorsConfigurationSource()

        val corsConfig = CorsConfiguration()
        corsConfig.allowCredentials = true
        corsConfig.addAllowedHeader("*")
        corsConfig.addAllowedMethod("*")
        corsConfig.maxAge = 3600L
        corsConfig.allowedOriginPatterns = listOf("*")

        source.registerCorsConfiguration("/**", corsConfig)

        val bean = FilterRegistrationBean(CorsFilter(source))
        bean.order = Ordered.HIGHEST_PRECEDENCE

        return bean
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }
}