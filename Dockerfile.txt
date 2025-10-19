# Multi-stage build
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app
COPY build.gradle .
COPY src ./src

RUN gradle build -x test

# Runtime stage
FROM --platform=linux/amd64 eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Copy the built JAR
COPY --from=builder /app/build/libs/*.jar app.jar

# Create non-root user
RUN addgroup -g 1001 -S zaman && adduser -u 1001 -S zaman -G zaman
RUN chown -R zaman:zaman /app
USER zaman

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

