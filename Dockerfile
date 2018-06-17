# Reference: https://spring.io/guides/gs/spring-boot-docker/

FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar

# Access Amazon SSM Parameter Store and populates parameters as environment variables:
# https://github.com/Droplr/aws-env
RUN wget https://github.com/Droplr/aws-env/raw/master/bin/aws-env-linux-amd64 -O /bin/aws-env && chmod +x /bin/aws-env

# This should only have effect when building the image with build time argument (--build-arg CREDENTIAL_FILE=<file path>)
# Aws Environment variables - https://docs.aws.amazon.com/cli/latest/userguide/cli-environment.html
ARG CREDENTIAL_FILE
COPY ${CREDENTIAL_FILE} /credentials
ENV AWS_SHARED_CREDENTIALS_FILE /credentials
ENV profile embedded

# Dependency on environment variables - dbhost, dbuser, dbpassword.
# Details are covered in the README.

# To ensure the process can receive the shutdown signal correctly, use exec command. For more details:
# https://docs.docker.com/engine/reference/builder/#shell-form-entrypoint-example
# additional info on exec: https://stackoverflow.com/questions/18351198/what-are-the-uses-of-the-exec-command-in-shell-scripts
ENTRYPOINT eval $(AWS_ENV_PATH=/production/gh-service AWS_REGION=ap-northeast-1 /bin/aws-env) && \\
exec java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${profile} -Ddatabase.host=${dbhost} -Ddatabase.password=${dbpassword} -jar /app.jar

# exec form
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=${profile}","-jar","/app.jar"]
