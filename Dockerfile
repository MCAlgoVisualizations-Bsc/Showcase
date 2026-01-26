# 1. Build Stage: Use Gradle with Java 25
# If gradle:jdk25 fails, we use a specific verified version
FROM gradle:8.12-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle shadowJar --no-daemon

# 2. Run Stage: Use Eclipse Temurin (Reliable Java 25 support)
FROM eclipse-temurin:25-jre
WORKDIR /app
# Copy the built jar from the previous stage
COPY --from=build /app/build/libs/*-all.jar server.jar

# Expose the internal port (always 25565 inside the box)
EXPOSE 25565

# Start command
CMD ["java", "-jar", "server.jar"]
