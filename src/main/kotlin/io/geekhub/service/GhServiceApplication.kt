package io.geekhub.service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Kotlin best practice:
 * https://blog.philipphauer.de/idiomatic-kotlin-best-practices/
 */
@SpringBootApplication
class GhServiceApplication

fun main(args: Array<String>) {
    runApplication<GhServiceApplication>(*args)
}
