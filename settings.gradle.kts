pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.springframework.boot") {
                useModule("org.springframework.boot:spring-boot-gradle-plugin:${requested.version}")
            }
        }
    }
}
include(
        "kotlin-cannon-base",
        "kotlin-cannon-api",
        "kotlin-cannon-generated",
        "kotlin-cannon-generator"
)
