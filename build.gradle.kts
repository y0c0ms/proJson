plugins {
    kotlin("jvm") version "2.3.20"
}

group = "proJson"
// CI passes -PreleaseVersion=<tag> on a release build; falls back to SNAPSHOT locally.
version = (findProperty("releaseVersion") as String?) ?: "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(23)
}

tasks.test {
    useJUnitPlatform()
}