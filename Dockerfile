FROM adoptopenjdk/openjdk11
MAINTAINER zhouleib1412@gmail.com

ENV APP_ROOT /opt/cannon
ENV SPRING_BOOT_PROFILE --spring.profiles.active=prod
ENV JAVA_OPTS -server -Xmx512m -Xms256m

RUN mkdir -p ${APP_ROOT}/etc ${APP_ROOT}/lib ${APP_ROOT}/bin
ADD kotlin-cannon-api/build/libs/kotlin-cannon-api.jar ${APP_ROOT}/lib/app.jar
WORKDIR $APP_ROOT

ENTRYPOINT java $JAVA_OPTS -jar /opt/cannon/lib/app.jar $SPRING_BOOT_PROFILE

EXPOSE 8080 8080




