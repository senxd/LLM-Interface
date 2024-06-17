plugins {
    kotlin("jvm") version "1.9.22"
}

group = "net.prismclient"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Web Parsing
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.json:json:20240303")

    // PDF Parsing
    implementation("net.sourceforge.tess4j:tess4j:5.11.0")
    implementation("org.apache.pdfbox:pdfbox:3.0.2")

    // Logger
    implementation("org.apache.logging.log4j:log4j-core:2.23.1")
    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.23.1")

    // Other
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

kotlin {
    jvmToolchain(18)
}