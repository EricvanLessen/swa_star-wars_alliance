FROM maven:3.9.9-eclipse-temurin-25 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /app/target/player-service-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/application.properties config/application.properties

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]