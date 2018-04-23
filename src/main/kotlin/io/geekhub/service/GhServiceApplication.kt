package io.geekhub.service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GhServiceApplication

fun main(args: Array<String>) {
    runApplication<GhServiceApplication>(*args)
}
