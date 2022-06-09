# https://spring.io/guides/gs/spring-boot-docker/
# https://github.com/docker/for-mac/issues/1922
FROM amazoncorretto:17

VOLUME /tmp
ARG JAR_FILE
# this passed in argument is for application to connect to Cloud SQL using the service account as described in this page:
# https://cloud.google.com/docs/authentication/production
ARG SERVICE_ACCOUNT_JSON
COPY ${JAR_FILE} /app.jar
COPY ${SERVICE_ACCOUNT_JSON} /service_account.json
ENV PROFILE default
ENV GOOGLE_APPLICATION_CREDENTIALS /service_account.json
ENV JVM_ARGS='-Dadd-exports=java.base/sun.nio.ch=ALL-UNNAMED -Dadd-opens=java.base/java.lang=ALL-UNNAMED -Dadd-opens=java.base/java.lang.reflect=ALL-UNNAMED -Dadd-opens=java.base/java.io=ALL-UNNAMED -Dadd-opens=java.base/java.util=ALL-UNNAMED -Dadd-exports=jdk.unsupported/sun.misc=ALL-UNNAMED'

ENTRYPOINT ["sh", "-c", "java $JVM_ARGS -Djava.security.egd=file:/dev/./urandom -Djdk.tls.client.protocols='TLSv1,TLSv1.1,TLSv1.2' -Dspring.profiles.active=$PROFILE -jar /app.jar"]
