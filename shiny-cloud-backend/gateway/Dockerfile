# ---- builder ----
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /build

COPY pom.xml .
COPY common/pom.xml common/
COPY gateway/pom.xml gateway/
COPY service/pom.xml service/
RUN mvn -pl gateway -am dependency:go-offline -B || true

COPY gateway ./gateway
RUN mvn -pl gateway -am package -DskipTests -B

# ---- runner ----
FROM bellsoft/liberica-openjre-alpine:17
WORKDIR /app

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY --from=builder /build/gateway/target/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]