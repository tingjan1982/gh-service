# Reference: https://spring.io/guides/gs/spring-boot-docker/

FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar

# Access Amazon SSM Parameter Store and populates parameters as environment variables:
# https://github.com/Droplr/aws-env
# https://docs.aws.amazon.com/cli/latest/userguide/cli-environment.html
RUN wget https://github.com/Droplr/aws-env/raw/master/bin/aws-env-linux-amd64 -O /bin/aws-env && chmod +x /bin/aws-env

# This should only have effect when building the image with build time argument (--build-arg CREDENTIAL_FILE=<file path>)
ARG CREDENTIAL_FILE
COPY ${CREDENTIAL_FILE} /credentials
ENV AWS_SHARED_CREDENTIALS_FILE /credentials
ENV profile embedded

# Dependency on environment variables - dbhost, dbuser, dbpassword.
# Details are covered in the README.
ENTRYPOINT eval $(AWS_ENV_PATH=/production/gh-service AWS_REGION=ap-northeast-1 /bin/aws-env) && \\
java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${profile} -Ddatabase.host=${dbhost} -Ddatabase.password=${dbpassword} -jar /app.jar

# exec form
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=${profile}","-jar","-Ddatabase.host=${dbhost}","/app.jar"]
