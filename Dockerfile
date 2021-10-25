FROM gradle:6.9-jdk8 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:8-jre-slim
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/calendar-api-1.0-SNAPSHOT.jar /app/calendar-api-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/app/calendar-api-1.0-SNAPSHOT.jar"]
