package com.github.b1412

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@SpringBootApplication(
        exclude = [org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration::class],
        scanBasePackages = ["com.github.b1412.cannon","com.github.b1412.security"]
)

class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
