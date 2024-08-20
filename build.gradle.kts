import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val mockkVersion = "1.13.12"
val testContainersVersion = "1.20.1"
val logstashVersion = "8.0"
val logbackSyslog4jVersion = "1.0.0"

plugins {
    val kotlinVersion = "2.0.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
    idea
    kotlin("kapt") version kotlinVersion
}

java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
    maven("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("com.zaxxer:HikariCP")
    implementation("org.postgresql:postgresql")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("ch.qos.logback:logback-classic")

    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")

    implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    implementation("com.papertrailapp:logback-syslog4j:$logbackSyslog4jVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage")
        exclude(group = "org.mockito")
    }

    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")

    testImplementation("io.mockk:mockk:$mockkVersion")
}

idea {
    module {
        isDownloadJavadoc = true
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    this.archiveFileName.set("app.jar")
}
