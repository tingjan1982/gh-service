package io.geekhub.service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.runApplication

/**
 * Kotlin best practice:
 * https://blog.philipphauer.de/idiomatic-kotlin-best-practices/
 *
 * Scoping functions:
 * https://medium.com/@elye.project/mastering-kotlin-standard-functions-run-with-let-also-and-apply-9cd334b0ef84
 *
 * Disable JPA Auto Configuration:
 * https://www.baeldung.com/spring-data-disable-auto-config
 */
@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class, DataSourceTransactionManagerAutoConfiguration::class, HibernateJpaAutoConfiguration::class])
class GhServiceApplication

fun main(args: Array<String>) {
    runApplication<GhServiceApplication>(*args)
}
