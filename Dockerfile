# ── Build ────────────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Descarga dependencias primero (aprovecha caché de Docker si el pom no cambia)
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

# ── Run ──────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/mayosalud-1.0.0.jar app.jar

EXPOSE 8080

# Render.com inyecta la variable PORT; Spring Boot la recibe como server.port
CMD java -Dserver.port=${PORT:-8080} \
         -XX:+UseContainerSupport \
         -XX:MaxRAMPercentage=75 \
         -jar app.jar
