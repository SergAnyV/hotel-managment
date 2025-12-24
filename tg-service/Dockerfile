FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/tg-service-*.jar /app/tg-service.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "/app/tg-service.jar"]