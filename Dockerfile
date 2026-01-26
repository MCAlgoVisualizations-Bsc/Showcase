# 1. Build Stage: Use Java to build the jar
FROM gradle:jdk25 AS build
WORKDIR /app
COPY . .
RUN gradle shadowJar --no-daemon

# 2. Run Stage: A lightweight image to just run it
FROM openjdk:25-slim
WORKDIR /app
# Copy the built jar from the previous stage
COPY --from=build /app/build/libs/*-all.jar server.jar

# Expose the internal port (always 25565 inside the box)
EXPOSE 25565

# Start command
CMD ["java", "-jar", "server.jar"]
