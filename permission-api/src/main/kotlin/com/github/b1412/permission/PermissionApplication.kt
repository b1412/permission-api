package com.github.b1412.permission

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.availability.ApplicationAvailabilityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration

@SpringBootApplication(
    scanBasePackages = [
        "com.github.b1412.*"
    ],
    exclude = [
        SecurityAutoConfiguration::class,
        ApplicationAvailabilityAutoConfiguration::class]
)
class PermissionApplication