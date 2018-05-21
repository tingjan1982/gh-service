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


## Reference

### Issue Tracking Configuration

IntelliJ YouTrack Plugin
> Used to browse YouTrack issues and issue YT commands directly.

Link Commit to YouTrack Issue
> Add YouTrack installation URL under Version Control -> Issue Navigation
> Add GitHub integration in YouTrack. 

### Teamcity Configuration

####
Teamcity Server (https://hub.docker.com/r/jetbrains/teamcity-server/)

> Pull Docker Image:
`sudo docker pull jetbrains/teamcity-server`

> Run Docker Container:
`sudo docker run -it --name teamcity-server-instance -v /Users/jlin/teamcity/data:/data/teamcity_server/datadir -v /Users/jlin/teamcity/logs:/data/teamcity_server/logs -p 8111:8111 jetbrains/teamcity-server`

The command runs a server docker container, binds data directory, logs directory and port.

####
Teamcity Agent (https://hub.docker.com/r/jetbrains/teamcity-agent/)

> Pull Docker Image:
`sudo docker pull jetbrains/teamcity-agent`

> Run Docker Container with Docker daemon enabled:
`sudo docker run -it -e SERVER_URL="192.168.2.244:8111" -v /Users/jlin/teamcity-agent/conf:/data/teamcity_agent/conf -v docker_volumes:/var/lib/docker --privileged -e DOCKER_IN_DOCKER=start --name teamcity-agent jetbrains/teamcity-agent`

The commands run an agent docker container and binds the conf directory. 

Note: when the agent is connected to the server, it is unauthorized. It is necessary to go into the Unauthorized
tab under Agents to authorize it.   

Copyright (c) 2018 Joe Lin