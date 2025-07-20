# Multi-stage build for optimized image size
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# Copy pom.xml first for better layer caching
COPY pom.xml ./

# Download dependencies (this layer will be cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Production stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/seat-booking-management-*.jar app.jar

# Expose the application port
EXPOSE 8080

# Set JVM options for containerized environment
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]