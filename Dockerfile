FROM node:14 as front
ENV PATH /app/node_modules/.bin:$PATH
COPY package.json ./
COPY package-lock.json ./
RUN npm run build
COPY . ./
# CMD ["npm", "start"]
# EXPOSE 3000

FROM gradle:jdk11-openj9 AS build
COPY --chown=gradle:gradle . /home/gradle/src
COPY --from=front  build .
WORKDIR /home/gradle/src
RUN gradle build --no-daemon 

FROM openjdk:11-jdk
# ARG JAR_FILE=target/*.jar
# RUN chmod +x gradlew
# RUN gradlew build
# CMD ["./gradlew", "build"]
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
# EXPOSE 8080
