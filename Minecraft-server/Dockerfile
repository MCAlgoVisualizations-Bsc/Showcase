# Build stage - compiles your code
FROM eclipse-temurin:25-jdk as builder
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew shadowJar

# Runtime stage - runs the server
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar server.jar
EXPOSE 25565
CMD ["java", "-Xms1G", "-Xmx2G", "-jar", "server.jar"]
