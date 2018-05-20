package io.geekhub.service.shared.config

import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpMethod
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
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
import javax.sql.DataSource


@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter(), InitializingBean {

    @Autowired
    lateinit var dataSource: DataSource

    @Autowired
    lateinit var environment: Environment

    override fun afterPropertiesSet() {
        val classPathResource = ClassPathResource("security-schema.ddl", SecurityConfig::class.java)
        ResourceDatabasePopulator(classPathResource).execute(dataSource)
    }

    /**
     * The URL patterns do not need to include the context path.
     *
     * CSRF reference:
     * https://docs.spring.io/spring-security/site/docs/5.0.5.RELEASE/reference/htmlsingle/#csrf
     * https://security.stackexchange.com/questions/166724/should-i-use-csrf-protection-on-rest-api-endpoints
     */
    override fun configure(http: HttpSecurity) {
        http.csrf().csrfTokenRepository(csrfTokenRepository())
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/users").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/csrf-token").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and().httpBasic()
    }

    @Bean
    fun csrfTokenRepository(): CsrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()

    override fun configure(auth: AuthenticationManagerBuilder) {
        val passwordEncoder = this.passwordEncoder()
        auth.inMemoryAuthentication().passwordEncoder(passwordEncoder)
                .withUser("user").password(passwordEncoder.encode("password")).roles("USER")

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

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }
}