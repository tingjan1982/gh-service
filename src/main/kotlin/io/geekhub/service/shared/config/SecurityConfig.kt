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
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
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
     */
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/users").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
                .and().httpBasic()
    }


    override fun configure(auth: AuthenticationManagerBuilder) {
        val passwordEncoder = this.passwordEncoder()
        auth.inMemoryAuthentication().passwordEncoder(passwordEncoder)
                .withUser("user").password(passwordEncoder.encode("password")).roles("USER")

        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .withUser("admin").password(passwordEncoder.encode("admin")).roles("ADMIN")
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }
}