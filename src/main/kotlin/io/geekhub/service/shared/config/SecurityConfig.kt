package io.geekhub.service.shared.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.csrf.CsrfTokenRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import javax.annotation.PreDestroy
import javax.sql.DataSource


@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter(), InitializingBean {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(SecurityConfig::class.java)
    }

    @Autowired
    lateinit var dataSource: DataSource

    @Autowired
    lateinit var ghServiceConfigProperties: GhServiceConfigProperties

    @Autowired
    lateinit var environment: Environment

    override fun afterPropertiesSet() {
        ResourceDatabasePopulator(ClassPathResource("security-schema.ddl", SecurityConfig::class.java)).let {
            when (this.determineDataSourceType()) {
                SecurityConfig.DataSourceType.POSTGRESQL -> it.addScript(ClassPathResource("/createAclSchemaPostgres.sql"))
                SecurityConfig.DataSourceType.EMBEDDED -> it.addScript(ClassPathResource("/createAclSchemaWithAclClassIdType.sql"))
            }

            it.execute(dataSource)
        }
    }

    // todo: this should really check for the actual data source rather than environment profile.
    fun determineDataSourceType(): DataSourceType {

        return when {
            this.environment.activeProfiles.any { it == "test" || it == "embedded" } -> DataSourceType.EMBEDDED
            else -> DataSourceType.POSTGRESQL
        }
    }

    @PreDestroy
    fun cleanUpSecuritySchema() {
        logger.info("Cleanup security schema...")

        when {
            this.environment.activeProfiles.any { it == "dev" || it == "test" || it == "embedded" } ||
                    this.environment.defaultProfiles.isNotEmpty() -> {

                val securitySchema = ClassPathResource("security-schema-drop.ddl", SecurityConfig::class.java)
                ResourceDatabasePopulator(securitySchema).execute(dataSource).let {
                    logger.info("Applied schema: ${securitySchema.path}")
                }
            }
        }
    }

    /**
     * The URL patterns do not need to include the context path.
     *
     * CSRF reference:
     * https://docs.spring.io/spring-security/site/docs/5.0.5.RELEASE/reference/htmlsingle/#csrf
     * https://security.stackexchange.com/questions/166724/should-i-use-csrf-protection-on-rest-api-endpoints
     */
    override fun configure(http: HttpSecurity) {

        this.ghServiceConfigProperties.apply {
            if (csrfEnabled) {
                http.csrf().csrfTokenRepository(csrfTokenRepository())
            } else {
                http.csrf().disable()
            }
        }

        http.cors()
                .and().authorizeRequests()
                .antMatchers("/csrf-token").hasRole("ADMIN")
                .anyRequest().permitAll()
                .and().httpBasic()
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

    @Bean
    fun csrfTokenRepository(): CsrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()

    override fun configure(auth: AuthenticationManagerBuilder) {
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

    /**
     * Expose UserDetailsService instance as bean for other services to use.
     */
    @Bean
    override fun userDetailsServiceBean(): UserDetailsService {
        return super.userDetailsServiceBean()
    }

    /**
     * Expose AuthenticationManager instance as bean for other services to use.
     */
    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    enum class DataSourceType {
        POSTGRESQL, EMBEDDED
    }
}