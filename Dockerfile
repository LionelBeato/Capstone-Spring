FROM openjdk:11-jdk
ARG JAR_FILE=target/*.jar
COPY build/libs/\*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
CMD [ "chmod +x gradlew && ./gradlew build" ]
EXPOSE 8080