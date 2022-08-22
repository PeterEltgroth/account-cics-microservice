FROM gradle:7.4.2-jdk11-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon 

FROM openjdk:11-jre-slim
MAINTAINER OpenLegacy <support@openlegacy.com>
# Constants
ENV JAR_FILE 'account-cics-microservice-1.0.0-SNAPSHOT.jar'
ENV APP_PATH '/usr/local'
COPY --from=build /home/gradle/src/build/libs/$JAR_FILE $APP_PATH/$JAR_FILE
COPY entrypoint.sh /
RUN chmod +x entrypoint.sh
# Launch the application
ENTRYPOINT ["./entrypoint.sh"]