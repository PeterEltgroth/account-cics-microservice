FROM openjdk:11-jre-slim
MAINTAINER OpenLegacy <support@openlegacy.com>
# Constants
ENV JAR_FILE 'account-cics-microservice-1.0.0-SNAPSHOT.jar'
ENV APP_PATH '/usr/local'
# Copy your fat jar to the container
COPY "build/libs/$JAR_FILE" $APP_PATH/$JAR_FILE
COPY entrypoint.sh /
RUN chmod +x entrypoint.sh
# Launch the application
ENTRYPOINT ["./entrypoint.sh"]