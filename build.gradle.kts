plugins {
    kotlin("jvm") version "2.0.0"
    application
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "work.kyokko"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("com.google.cloud:libraries-bom:26.39.0"))
    implementation("com.google.cloud:google-cloud-bigquery")
    implementation("org.tukaani:xz:1.10")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "work.kyokko.MainKt"
}
