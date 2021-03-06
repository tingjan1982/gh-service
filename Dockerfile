# https://spring.io/guides/gs/spring-boot-docker/
# https://github.com/docker/for-mac/issues/1922
FROM amazoncorretto:11

VOLUME /tmp
ARG JAR_FILE
# this passed in argument is for application to connect to Cloud SQL using the service account as described in this page:
# https://cloud.google.com/docs/authentication/production
ARG SERVICE_ACCOUNT_JSON
COPY ${JAR_FILE} /app.jar
COPY ${SERVICE_ACCOUNT_JSON} /service_account.json
ENV PROFILE default
ENV GOOGLE_APPLICATION_CREDENTIALS /service_account.json

##  Google Cloud Debugger Agent: https://cloud.google.com/debugger/docs/setup/java
#RUN  mkdir /opt/cdbg && \
#     wget -qO- https://storage.googleapis.com/cloud-debugger/compute-java/debian-wheezy/cdbg_java_agent_gce.tar.gz | \
#     tar xvz -C /opt/cdbg

ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -Djdk.tls.client.protocols='TLSv1,TLSv1.1,TLSv1.2' -Dspring.profiles.active=$PROFILE -jar /app.jar"]
