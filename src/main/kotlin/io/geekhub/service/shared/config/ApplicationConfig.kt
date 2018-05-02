package io.geekhub.service.shared.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource


@Configuration
@EnableJpaRepositories("io.geekhub.service")
@EnableTransactionManagement
class ApplicationConfig {

    @Bean
    fun dataSource(): DataSource {
        val builder = EmbeddedDatabaseBuilder()
        return builder.setType(EmbeddedDatabaseType.HSQL).build()
    }
    
//    @Bean
//    fun entityManagerFactory(): EntityManagerFactory {
//
//    }
}