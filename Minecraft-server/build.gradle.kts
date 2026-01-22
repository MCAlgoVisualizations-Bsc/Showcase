plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("net.minestom:minestom:2026.01.08-1.21.11") //Added Minstom dependency
    implementation("ch.qos.logback:logback-classic:1.4.14") // Added logging, file saving and formatting
}

tasks.test {
    useJUnitPlatform()
}