<a href="http://192.168.2.244:8111/viewType.html?buildTypeId=GhService_Build&guest=1">
    <img src="http://192.168.2.244:8111/app/rest/builds/buildType:(id:GhService_Build)/statusIcon"/>
</a>

# GeekHub Service

GeekHub REST API Service for GeekHub Interview Platform. (http://geekhub.tw) 

## Start the Service
 
### In Gradle

`./gradlew bootRun` 
 
### In Docker

Refer to .travis.yaml and Dockerfile for step by step instructions.

## Issue Tracking 

https://geekhub.myjetbrains.com/youtrack

IntelliJ YouTrack Plugin
> Used to browse YouTrack issues and issue YT commands directly.

Link Commit to YouTrack Issue
> Add YouTrack installation URL under Version Control -> Issue Navigation
> Add GitHub integration in YouTrack. 

## Useful Docker Commands

Start google cloud container

`sudo docker start gh-cloud`

`sudo docker exec -it gh-cloud ash`

Remove dangling images

`sudo docker rmi $(docker images -q -f dangling=true)`

## References

### Encrypt files used by Travis CI

https://docs.travis-ci.com/user/encrypting-files/

### Build Gradle project in Docker

https://stackoverflow.com/questions/46792438/build-gradle-project-inside-a-docker

### Dockerized AWS CLI

https://hub.docker.com/r/mesosphere/aws-cli/
