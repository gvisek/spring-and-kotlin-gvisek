package com.example.homework

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class SpringBootApp

fun main(args: Array<String>) {
    runApplication<SpringBootApp>(*args)
}
