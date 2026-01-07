# Use Java 17 (Spring Boot friendly)
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Give execute permission
RUN chmod +x mvnw

# Download dependencies (cache-friendly)
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose Spring Boot port
EXPOSE 8080

# Run the jar (wildcard safe)
CMD sh -c "java -jar target/*.jar"
