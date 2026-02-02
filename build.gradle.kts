plugins {
    java
    id("com.gradleup.shadow") version "9.3.0" //shadowjar
    kotlin("jvm") version "2.3.0"
}
// 1. Force Java Toolchain to 21 (This handles 'compileJava')
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

sourceSets {
    main {
        java {
            srcDirs("src")
        }
    }
}


group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    // Add the Kotlin Standard Library
    implementation(kotlin("stdlib"))

    // Your Testing Dependencies
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    //testImplementation("org.junit.jupiter:junit-jupiter")
    //testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Your Server Dependencies
    implementation("net.minestom:minestom:2026.01.08-1.21.11")
    implementation("ch.qos.logback:logback-classic:1.4.14")
}



tasks {
    test {
        useJUnitPlatform()
    }

    shadowJar {
        manifest {
            attributes["Main-Class"] = "Main"
        }
    }
}