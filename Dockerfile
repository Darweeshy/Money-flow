# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom first so dependencies are cached
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline -q

# Copy source and build
COPY src ./src
RUN ./mvnw package -DskipTests -q

# Extract layered jar
RUN java -Djarmode=layertools -jar target/*.jar extract --destination target/extracted

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine AS runtime

# Add non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy layered jar contents (least to most frequently changed)
COPY --from=builder --chown=appuser:appgroup /app/target/extracted/dependencies/ ./
COPY --from=builder --chown=appuser:appgroup /app/target/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=appuser:appgroup /app/target/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=appuser:appgroup /app/target/extracted/application/ ./

USER appuser

EXPOSE 8081

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+UseG1GC", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "org.springframework.boot.loader.launch.JarLauncher"]