# Build stage - compiles your code
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew clean shadowJar --no-daemon --stacktrace

# Runtime stage - runs the server
FROM eclipse-temurin:25-jre
WORKDIR /app

# Copy the specific JAR file (shadowJar creates a -all.jar)
COPY --from=builder /app/build/libs/Minecraft-server-*-all.jar server.jar

# Create necessary directories
RUN mkdir -p /app/world /app/logs

# Expose the Minecraft port
EXPOSE 25565

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD pgrep -f "java.*server.jar" > /dev/null || exit 1

# Start the server
CMD ["java", "-Xms1G", "-Xmx2G", "-XX:+UseG1GC", "-XX:+ParallelRefProcEnabled", "-XX:MaxGCPauseMillis=200", "-XX:+UnlockExperimentalVMOptions", "-XX:+DisableExplicitGC", "-XX:G1NewSizePercent=30", "-XX:G1MaxNewSizePercent=40", "-XX:G1HeapRegionSize=8M", "-XX:G1ReservePercent=20", "-XX:G1HeapWastePercent=5", "-XX:G1MixedGCCountTarget=4", "-XX:InitiatingHeapOccupancyPercent=15", "-XX:G1MixedGCLiveThresholdPercent=90", "-XX:G1RSetUpdatingPauseTimePercent=5", "-XX:SurvivorRatio=32", "-XX:+PerfDisableSharedMem", "-XX:MaxTenuringThreshold=1", "-jar", "server.jar"]
