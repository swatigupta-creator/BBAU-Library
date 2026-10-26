FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY backend backend
COPY frontend frontend
RUN mvn -f backend/pom.xml -DskipTests package
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/backend/target/bbau-library-1.0.0.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
