# Build: copy prebuilt app.jar or uncomment multi-stage to build inside container

# --- Runtime image ---
FROM eclipse-temurin:17-jre as runtime
ENV APP_HOME=/opt/app
WORKDIR ${APP_HOME}

# Expect app.jar to be present after: ./gradlew bootJar
# Copy from build context
COPY build/libs/app.jar app.jar

# JVM flags via env for tuning
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"
ENV SERVER_PORT=8080
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# --- Alternative: build inside the image (requires Gradle wrapper present) ---
# FROM eclipse-temurin:17-jdk as builder
# WORKDIR /workspace
# COPY . .
# RUN ./gradlew --no-daemon clean bootJar
# FROM eclipse-temurin:17-jre as runtime
# WORKDIR /opt/app
# COPY --from=builder /workspace/build/libs/app.jar app.jar
# ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
