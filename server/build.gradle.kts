plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.0"
}

sourceSets {
    main {
        java {
            srcDirs("src")
        }
    }
}

group = "io.github.mcalgovisualizations"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    // Server Dependencies
    implementation("net.minestom:minestom:2026.01.08-1.21.11")
    implementation("ch.qos.logback:logback-classic:1.5.13")

    // Project Dependency
    implementation(project(":lib"))

    // Testing Dependencies
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    test {
        useJUnitPlatform()
    }

    shadowJar {
        manifest {
            attributes["Main-Class"] = "Main"
        }
        archiveBaseName.set("minecraft-server")
        archiveClassifier.set("all")
    }

    build {
        dependsOn(shadowJar)
    }
}