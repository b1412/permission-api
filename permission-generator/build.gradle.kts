import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    val kotlinVersion = "1.4.20"
    jacoco
    id("org.springframework.boot") version "2.4.0"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm") version kotlinVersion
}

val jar: Jar by tasks
val bootJar: BootJar by tasks

bootJar.enabled = false
jar.enabled = true

java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven(url = "https://jitpack.io")
}

dependencies {
    api(project(":permission-base"))
    api("com.github.b1412:kotlin-code-generator:77e431bb75")
    api("com.github.b1412:generator-tasks:8acf3cd13a")

    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.jetbrains.kotlin:kotlin-reflect")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    val arrowVersion = "0.11.0"
    api("io.arrow-kt:arrow-core:$arrowVersion")
    api("io.arrow-kt:arrow-syntax:$arrowVersion")
    api("com.google.guava:guava:30.0-jre")
    api("mysql:mysql-connector-java:8.0.22")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict","-Xallow-result-return-type")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
