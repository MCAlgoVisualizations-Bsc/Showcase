# 1. Build Stage
# Using an image that provides JDK 25
FROM openjdk:25 AS build
WORKDIR /app

# Copy the Gradle wrapper files from your repo
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Copy your source code
COPY src/ src/

# Ensure the wrapper is executable and run the build
RUN chmod +x ./gradlew
RUN ./gradlew shadowJar --no-daemon

# 2. Run Stage
FROM openjdk:25-jdk-slim
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/build/libs/*-all.jar server.jar

# Minestom usually doesn't need an eula.txt, but good to have if you add plugins
RUN echo "eula=true" > eula.txt

EXPOSE 25565

# Start with enough memory for a Minestom instance
CMD ["java", "-Xmx2G", "-jar", "server.jar"]
