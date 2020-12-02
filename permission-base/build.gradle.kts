import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    val kotlinVersion = "1.4.20"
    id("org.springframework.boot") version "2.4.0"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
    kotlin("plugin.noarg") version kotlinVersion
    `maven-publish`
}

val jar: Jar by tasks
val bootJar: BootJar by tasks

bootJar.enabled = false
jar.enabled = true

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
}
noArg {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
}

java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven(url = "https://jitpack.io")
}
dependencies {
    val arrowVersion = "0.11.0"
    api("com.github.b1412:api-common:06fd37d214")
    api("com.github.b1412:kotlin-code-generator-meta:8c10be3699")

    api("org.jetbrains.kotlin:kotlin-reflect")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    springboot()
    graphql()
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
    api("com.fasterxml.jackson.datatype:jackson-datatype-hibernate5")

    arrow(arrowVersion)
    api("org.jooq:joor-java-8:0.9.12")
    api("io.github.microutils:kotlin-logging:1.7.6")
    api("io.jsonwebtoken:jjwt:0.7.0")
    api("commons-beanutils:commons-beanutils:1.9.4")

    api("mysql:mysql-connector-java:8.0.22")
    api("com.vladmihalcea:hibernate-types-52:2.10.0")


    testApi("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testApi("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
        exclude(module = "mockito-core")
    }
    testApi("com.ninja-squad:springmockk:1.1.2")
    testApi("org.assertj:assertj-core:3.18.1")

    testRuntimeOnly("com.h2database:h2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}


fun DependencyHandlerScope.springboot() {
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-freemarker")
    api("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    api("org.springframework.boot:spring-boot-starter-undertow")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-data-redis")
    api("org.springframework.boot:spring-boot-autoconfigure-processor")
}

fun DependencyHandlerScope.arrow(arrowVersion: String) {
    api("io.arrow-kt:arrow-fx:$arrowVersion")
    api("io.arrow-kt:arrow-optics:$arrowVersion")
    api("io.arrow-kt:arrow-syntax:$arrowVersion")
}

fun DependencyHandlerScope.graphql() {
    api("com.graphql-java-kickstart:playground-spring-boot-starter:5.10.0")
    api("com.graphql-java:graphql-spring-boot-starter:5.0.2")
    api("com.graphql-java:graphiql-spring-boot-starter:5.0.2")
    api("com.graphql-java:graphql-java-tools:5.2.4")
}
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xallow-result-return-type")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/b1412/permission-api")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                artifactId = tasks.jar.get().archiveBaseName.get()
            }

        }
    }
}
