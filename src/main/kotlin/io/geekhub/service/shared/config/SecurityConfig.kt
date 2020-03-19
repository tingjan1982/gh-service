package io.geekhub.service.shared.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.*
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter


@EnableWebSecurity
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
                .mvcMatchers("/**").permitAll()
                .mvcMatchers("/specializations").authenticated()
                .mvcMatchers("/questions").authenticated()
                //.mvcMatchers("/api/private-scoped").hasAuthority("SCOPE_read:messages")
                .and()
                .oauth2ResourceServer().jwt();

        /*http.cors()
                .and().authorizeRequests()
                .antMatchers("/csrf-token").hasRole("ADMIN")
                .anyRequest().permitAll()
                .and().httpBasic()*/
    }

    @Bean
    fun jwtDecoder(): JwtDecoder? {
        val jwtDecoder: NimbusJwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuer) as NimbusJwtDecoder
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

        val configAutenticacao = CorsConfiguration()
        configAutenticacao.allowCredentials = true
        configAutenticacao.addAllowedOrigin("*")
        configAutenticacao.addAllowedHeader("*")
        configAutenticacao.addAllowedMethod("*")
        configAutenticacao.setMaxAge(3600L)
        source.registerCorsConfiguration("/**", configAutenticacao)

        val bean = FilterRegistrationBean(CorsFilter(source))
        bean.order = Ordered.HIGHEST_PRECEDENCE

        return bean
    }

    /*override fun configure(auth: AuthenticationManagerBuilder) {
        val passwordEncoder = this.passwordEncoder()
        auth.userDetailsService(this.userDetailsManager())

        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .withUser("admin").password(passwordEncoder.encode("admin")).roles("ADMIN")
    }

    @Bean
    fun userDetailsManager(): JdbcUserDetailsManager {
        return JdbcUserDetailsManager().also {
            it.setDataSource(this.dataSource)
        }
    }

    */
    /**
     * Expose UserDetailsService instance as bean for other services to use.
     *//*
    @Bean
    override fun userDetailsServiceBean(): UserDetailsService {
        return super.userDetailsServiceBean()
    }

    */
    /**
     * Expose AuthenticationManager instance as bean for other services to use.
     *//*
    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }*/

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }
}