FROM gradle:8.10-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon --parallel -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 9200
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
  CMD wget -qO- http://localhost:9200/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
