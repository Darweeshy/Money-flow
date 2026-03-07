# ============================================================
#  MoneyFlow — User Service
#  Multistage Dockerfile
#  Final image: Eclipse Temurin 21 JRE Alpine (~85MB)
# ============================================================

# ── Stage 1: Build ──────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom first — Docker caches this layer
# so dependencies are only re-downloaded when pom.xml changes
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -q

# Copy source and build
COPY src ./src
RUN ./mvnw package -DskipTests -q

# Extract layered jar for optimised layer caching
RUN java -Djarmode=layertools -jar target/*.jar extract --destination target/extracted

# ── Stage 2: Runtime ─────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

# Add non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy layered jar contents in order of least to most frequently changed
# This maximises Docker layer cache reuse on rebuilds
COPY --from=builder --chown=appuser:appgroup /app/target/extracted/dependencies/ ./
COPY --from=builder --chown=appuser:appgroup /app/target/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=appuser:appgroup /app/target/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=appuser:appgroup /app/target/extracted/application/ ./

USER appuser

EXPOSE 8081

# Use JVM flags optimised for containers:
# -XX:+UseContainerSupport    — respect container memory limits
# -XX:MaxRAMPercentage=75.0   — use 75% of container RAM for heap
# -XX:+UseG1GC                — G1 garbage collector, good for low latency
# -Djava.security.egd         — faster startup by using /dev/urandom
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+UseG1GC", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "org.springframework.boot.loader.launch.JarLauncher"]