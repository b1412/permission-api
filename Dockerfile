FROM openjdk:8
MAINTAINER CannonDev

ENV APP_ROOT /opt/cannon
ENV APP_VERSION 0.0.1
ARG BUILD_NUMBER=SNAPSHOT
ENV SPRING_BOOT_PROFILE --spring.profiles.active=prod
ENV JAVA_OPTS -server -Xmx512m -Xms256m

RUN mkdir -p ${APP_ROOT}/etc ${APP_ROOT}/lib ${APP_ROOT}/bin
ADD build/libs/cannon-${APP_VERSION}-${BUILD_NUMBER}.jar ${APP_ROOT}/lib/app.jar
WORKDIR $APP_ROOT

ENTRYPOINT java $JAVA_OPTS -jar /opt/cannon/lib/app.jar $SPRING_BOOT_PROFILE

EXPOSE 8080 8080

