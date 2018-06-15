<a href="http://192.168.2.244:8111/viewType.html?buildTypeId=GhService_Build&guest=1">
    <img src="http://192.168.2.244:8111/app/rest/builds/buildType:(id:GhService_Build)/statusIcon"/>
</a>

# gh-service

GeekHub REST API Service

## Run the service

### Embedded Mode

> Start the service with an embedded database.

`./gradlew bootRun`

### Postgres Mode 

https://hub.docker.com/_/postgres/       

> Pull Docker image:

`sudo docker pull postgres:alpine`

This command gets the alpine version of the Postgres image.

> Run the postgres in the backend:

`sudo docker run --name gh-service-postgres -e POSTGRES_PASSWORD=%90nw3Uhw -e POSTGRES_DB=gh-service -p 5432:5432 -d postgres:alpine`

> Connect to database via psql client:

`sudo docker run -it --rm --link gh-service-postgres:postgres postgres:alpine psql -h postgres -U postgres -d gh-service`
 
> Run in Gradle

`./gradlew bootRun -Pspring.profiles.active=default` 
 
> Run in Docker

`./gradlew docker`

This will build the Spring Boot execution jar and build the Docker image 
with name as specified by the Dockerfile.

> Run in Docker container

`sudo docker run --rm -p 8080:8080 --name gh-service-api io.geekhub/gh-service`

### Aws ECS

> Login to EC2 instance. Remember to change the pem file mode to 600, and expose port 22 in the EC2 configuration.

`ssh -i <pem> ec2-user@<ip>`


## Reference

### Issue Tracking Configuration

IntelliJ YouTrack Plugin
> Used to browse YouTrack issues and issue YT commands directly.

Link Commit to YouTrack Issue
> Add YouTrack installation URL under Version Control -> Issue Navigation
> Add GitHub integration in YouTrack. 

### Teamcity Configuration

#### Teamcity Server (https://hub.docker.com/r/jetbrains/teamcity-server/)

> Pull Docker Image:
`sudo docker pull jetbrains/teamcity-server`

> Run Docker Container:
`sudo docker run -it --name teamcity-server-instance -v /Users/jlin/teamcity/data:/data/teamcity_server/datadir -v /Users/jlin/teamcity/logs:/data/teamcity_server/logs -p 8111:8111 jetbrains/teamcity-server`

The command runs a server docker container, binds data directory, logs directory and port.

> Upgrade Teamcity Server

* Execute the Pull Docker Image command.
* Stop the currently running teamcity server container.
* Remove the stopped teamcity server container. 
* Execute the Run Docker Container command. 

#### Teamcity Agent (https://hub.docker.com/r/jetbrains/teamcity-agent/)

> Pull Docker Image:
`sudo docker pull jetbrains/teamcity-agent`

> Run Docker Container with Docker daemon enabled:
`sudo docker run -it -e SERVER_URL="192.168.2.244:8111" -v /Users/jlin/teamcity-agent/conf:/data/teamcity_agent/conf -v docker_volumes:/var/lib/docker --privileged -e DOCKER_IN_DOCKER=start --name teamcity-agent jetbrains/teamcity-agent`

The commands run an agent docker container and binds the conf directory. 

Note: when the agent is connected to the server, it is unauthorized. It is necessary to go into the Unauthorized
tab under Agents to authorize it.   

#### ELK on Docker (http://elk-docker.readthedocs.io/)

> Pull Docker Image:

`sudo docker pull sebp/elk:latest`

> Create bridge work in Docker to connect containers.

`sudo docker network create -d bridge elknet`

> Run Docker Container:

`sudo docker run -p 5601:5601 -p 9200:9200 -p 5044:5044 -e MAX_MAP_COUNT=262144 -it --network=elknet --name elk sebp/elk`

Refer to the prerequisite section for details on the MAX_MAP_COUNT environment variable.

#### Filebeat on Docker (https://www.elastic.co/guide/en/beats/filebeat/current/running-on-docker.html)

> Pull Docker Image:

`docker pull docker.elastic.co/beats/filebeat:6.2.4`

> Run Docker Container:

`sudo docker run -it  
-v <gh-service home>/src/main/resources/filebeat/filebeat.yml:/usr/share/filebeat/filebeat.yml 
-v <gh-service home>/src/main/resources/filebeat/logstash-beats.crt:/etc/pki/tls/certs/logstash-beats.crt 
-v /Users/jlin/IdeaProjects/gh-service/:/logs/ 
--network=elknet --name filebeat docker.elastic.co/beats/filebeat:6.2.4`

This Docker run command binds filebeat.yml for filebeat configuration, binds logstash-beats.crt for server side authentication, and gh-service directory
as the log source defined in filebeat.yml.

##### Reference: 

Filebreat for Spring Boot: http://wayne-yuen.blogspot.com/2017/03/setup-elk-stack-to-monitor-spring.html
Filebeat reference yml: https://www.elastic.co/guide/en/beats/filebeat/current/filebeat-reference-yml.html
Dockerized AWS CLI: https://hub.docker.com/r/mesosphere/aws-cli/

Aws Parameter Store:
https://medium.com/@tdi/ssm-parameter-store-for-keeping-secrets-in-a-structured-way-53a25d48166a
https://www.whaletech.co/2017/08/01/Secure-Credentials-for-ECS-Tasks.html
https://aws.amazon.com/blogs/compute/managing-secrets-for-amazon-ecs-applications-using-parameter-store-and-iam-roles-for-tasks/

utility to store parameters as environment variables - https://github.com/Droplr/aws-env

Remove dangling images

`docker rmi $(docker images -q -f dangling=true)`

Copyright (c) 2018 Joe Lin