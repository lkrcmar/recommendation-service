FROM eclipse-temurin:17-jdk

COPY build/libs/recommendation-service-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
