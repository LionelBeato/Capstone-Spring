FROM openjdk:11-jdk
ARG JAR_FILE=target/*.jar
# CMD "chmod +x gradlew && ./gradlew build"
CMD "sh build.sh"
COPY build/libs/\*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080