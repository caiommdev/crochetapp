# syntax=docker/dockerfile:1
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw 2>/dev/null || true
RUN ./mvnw -q -f pom.xml -pl services/catalog-service -am package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/services/catalog-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
