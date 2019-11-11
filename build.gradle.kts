import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.3.50"
    jacoco
    id("org.springframework.boot") version "2.2.0.RC1"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        html.isEnabled = false
    }
    classDirectories.setFrom(
            sourceSets.main.get().output.asFileTree.matching {
                exclude("/*.class")
            }
    )
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

group = "cannon"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
}

repositories {
    mavenCentral()
    maven(url = "https://repo.spring.io/milestone")
    maven(url = "https://dl.bintray.com/arrow-kt/arrow-kt/")
    maven(url = "https://oss.jfrog.org/artifactory/oss-snapshot-local/")
}

dependencies {
    val arrowVersion = "0.10.0-SNAPSHOT"

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    springboot()
    implementation("io.jsonwebtoken:jjwt:0.7.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("mysql:mysql-connector-java:6.0.5")
    arrow(arrowVersion)
    graphql()

    implementation("org.jooq:joor-java-8:0.9.12")
    implementation("io.github.microutils:kotlin-logging:1.7.6")
    implementation("commons-beanutils:commons-beanutils:1.9.4")
    implementation("org.codehaus.groovy:groovy-jsr223:2.4.3")

    
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
        exclude(module = "mockito-core")
    }
    testImplementation("com.ninja-squad:springmockk:1.1.2")

    runtimeOnly("com.h2database:h2")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}


fun DependencyHandlerScope.springboot() {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-freemarker")
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-undertow")
}

fun DependencyHandlerScope.arrow(arrowVersion: String) {
    implementation("io.arrow-kt:arrow-fx:$arrowVersion")
    implementation("io.arrow-kt:arrow-optics:$arrowVersion")
    implementation("io.arrow-kt:arrow-syntax:$arrowVersion")
}

fun DependencyHandlerScope.graphql() {
    implementation("com.graphql-java-kickstart:playground-spring-boot-starter:5.10.0")
    implementation("com.graphql-java:graphql-spring-boot-starter:5.0.2")
    implementation("com.graphql-java:graphiql-spring-boot-starter:5.0.2")
    implementation("com.graphql-java:graphql-java-tools:5.2.4")
}
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
