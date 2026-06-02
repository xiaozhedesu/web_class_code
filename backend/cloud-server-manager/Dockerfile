# builder
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app
COPY . .
RUN mvn package -DskipTests

# runner
FROM bellsoft/liberica-openjre-alpine:17

WORKDIR /app
COPY --from=builder /app/target/cloud-server-manager-app.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]