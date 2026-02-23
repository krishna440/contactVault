FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

CMD ["sh", "-c", "java -Dserver.port=$PORT -Dserver.address=0.0.0.0 -jar app.jar"]