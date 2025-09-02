FROM eclipse-temurin:21-jdk

WORKDIR /rococo
ENV TZ=Europe/Moscow
COPY ./gradle ./gradle
COPY ./rococo-autotest ./rococo-autotest
COPY ./rococo-grpc ./rococo-grpc
COPY ./gradlew ./
COPY ./build.gradle ./
COPY ./settings.gradle ./
COPY ./gradle.properties ./

RUN chmod +x ./gradlew
CMD ./gradlew test -Dtest.env=docker -Dbrowser=${BROWSER} -Duser.timezone=Europe/Moscow