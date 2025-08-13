# ===== Stage 1: Build the application =====
FROM gradle:8.10.0-jdk21 AS builder

WORKDIR /app

# Copy build scripts first (better caching of dependencies)
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon || return 0

# Copy the rest of the source code
COPY . .

# Build Spring Boot fat JAR
RUN ./gradlew bootJar --no-daemon


# ===== Stage 2: Create minimal runtime image =====
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]