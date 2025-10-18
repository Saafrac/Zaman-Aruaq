# Multi-stage build
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app
COPY build.gradle .
COPY src ./src

RUN gradle build -x test

# Runtime stage
FROM openjdk:17-jre-slim

WORKDIR /app

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the built JAR
COPY --from=builder /app/build/libs/*.jar app.jar

# Create non-root user
RUN groupadd -r zaman && useradd -r -g zaman zaman
RUN chown -R zaman:zaman /app
USER zaman

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
