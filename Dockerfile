FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/*.jar app.jar

ENTRYPOINT ["sh","-c","java -jar app.jar --server.port=$PORT"]