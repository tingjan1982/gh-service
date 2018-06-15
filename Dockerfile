# Reference: https://spring.io/guides/gs/spring-boot-docker/

FROM openjdk:8-jdk-alpine
VOLUME /tmp
ENV profile embedded
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=${profile}","-jar","/app.jar"]
