# Production-ready Dockerfile for resume-analyzer
# - Uses a lightweight Eclipse Temurin Java 17 JRE image
# - Copies the pre-built runnable JAR (target/resume-analyzer-1.0.0.jar)
# - Exposes port 8080 and uses ENTRYPOINT to run the app

FROM eclipse-temurin:17-jre-focal

ARG JAR_FILE=target/resume-analyzer-1.0.0.jar

# Ensure the jar exists in build context; copy as app.jar
COPY ${JAR_FILE} /app.jar

# Use a non-root user in production images where possible (optional)
# (left as root here for simplicity; adapt by adding a dedicated user if required)

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
