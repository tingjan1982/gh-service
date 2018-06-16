<a href="http://192.168.2.244:8111/viewType.html?buildTypeId=GhService_Build&guest=1">
    <img src="http://192.168.2.244:8111/app/rest/builds/buildType:(id:GhService_Build)/statusIcon"/>
</a>

# gh-service

GeekHub REST API Service is the backend to support Geek Hub Interview Platform. 

## Get Started

### Embedded Mode

Start the service with an embedded HSQL database.

`./gradlew bootRun`

### Postgres Mode 

Start the service connecting a Postgres database.

#### Setup Postgres (https://hub.docker.com/_/postgres/)       

Pull the alpine vesion of the Postgres Docker image:

`sudo docker pull postgres:alpine`

Run the Postgres container with some configuration parameters:

`sudo docker run --name gh-service-postgres -e POSTGRES_PASSWORD=%90nw3Uhw -e POSTGRES_DB=gh-service -p 5432:5432 -d postgres:alpine`

Verify the database connection by launching a psql client:

`sudo docker run -it --rm --link gh-service-postgres:postgres postgres:alpine psql -h postgres -U postgres -d gh-service`

#### Start the Service
 
In Gradle

`./gradlew bootRun -Pspring.profiles.active=default` 
 
In Docker

Build the Docker image: 

`./gradlew docker`

This will build the Spring Boot executable jar and build the Docker image 
with name as specified by the Dockerfile.

Verify the built image:

`sudo docker images joelin/gh-service` 

Run in Docker container

`sudo docker run --rm -p 8080:8080 -e profile=default --name gh-service joelin/gh-service`

The default profile will start the backend service connecting to the local Postgres container.

### Aws RDS Mode

Prerequisite:
* Provision a Postgres RDS instance with public accessible IP.

Start the service with dev profile:

`./gradlew bootRun -Pspring.profiles.active=dev`

or

`sudo docker run --rm -p 8080:8080 -e profile=dev --name gh-service joelin/gh-service`

## Aws Cloud Deployment

We use Amazon ECS to deploy gh-service. To enable this, we need to download the aws cli.  

### Create Properties via Parameter Store

* Create Aws credentials using the Aws IAM console to create an user with the following permissions:
    * AmazonECS_FullAccess
    * AmazonSSMFullAccess
    
Upon user creation, you should see access key and secret key, which you will use to configure aws cli. Run this:

`aws cli`

And supply the user access key and secret key, and region, in our case, is using ap-northeast-1 (Tokyo DC).                                    
More details on region can be found [here](https://docs.aws.amazon.com/general/latest/gr/rande.html):     
* Create the required application properties in SSM (Simple Systems Manager) Parameter Store

```
aws ssm put-parameter --name /production/gh-service/dbhost --type "SecureString" --value "geekhub-datastore.c6hjhdh7b1sb.ap-northeast-1.rds.amazonaws.com" 
aws ssm put-parameter --name /production/gh-service/dbuser --type "SecureString" --value "dbadmin" 
aws ssm put-parameter --name /production/gh-service/dbpassword --type "SecureString" --value "%90nw3Uhw" 
```

### Build and Verify Docker Container

* Provision the Docker image

Go to Project root directory and execute the following commands:
```

cp ~/.aws/credentials credentials (this coipes the Aws credentials file to current project directory for late use)

./gradlew docker 

Or to build image manually:

sudo docker build -t joelin/gh-service:latest --build-arg CREDENTIAL_FILE=credentials .  
```
* Verify the service container

`sudo docker run --rm -e profile=stagingjoelin/gh-service`

You should see the service launch with the staging profile and start successfully. 

### Provision the Docker Container with Amazon ECS

* Create a IAM role and assign to ECS task in order to access the parameters previously configured in Parameter Store.
The required permission on the role is AmazonSSMReadOnlyAccess.

* Create ECS. This involves creating the following components:
    * Task Definition
        * assign the created IAM role to ECS task definition.
        * assign a profile in the container definition section. 
    * Cluster
    * Service
    
After completing the steps above, you should have an active gh-service running on Amazon ECS.

### Reference 

Aws Parameter Store:
https://medium.com/@tdi/ssm-parameter-store-for-keeping-secrets-in-a-structured-way-53a25d48166a
https://www.whaletech.co/2017/08/01/Secure-Credentials-for-ECS-Tasks.html
https://aws.amazon.com/blogs/compute/managing-secrets-for-amazon-ecs-applications-using-parameter-store-and-iam-roles-for-tasks/
utility to store parameters as environment variables - https://github.com/Droplr/aws-env

## Reference

## SSH to EC2 Instance

Amazon ECS provisions the Docker containers in EC2. To login to EC2 instance, you need to create key pair and select
it during ECS configuration. You should then be able to download the .pem file necessary to SSH into the EC2 instance.
Remember to change the pem file mode to 600, and expose port 22 in the EC2 configuration.

`ssh -i <pem> ec2-user@<ip>`


### Issue Tracking Configuration

IntelliJ YouTrack Plugin
> Used to browse YouTrack issues and issue YT commands directly.

Link Commit to YouTrack Issue
> Add YouTrack installation URL under Version Control -> Issue Navigation
> Add GitHub integration in YouTrack. 

### Teamcity Configuration

#### Teamcity Server (https://hub.docker.com/r/jetbrains/teamcity-server/)

Pull Docker image:

`sudo docker pull jetbrains/teamcity-server`

Run Docker container:

`sudo docker run -it --name teamcity-server-instance -v /Users/jlin/teamcity/data:/data/teamcity_server/datadir -v /Users/jlin/teamcity/logs:/data/teamcity_server/logs -p 8111:8111 jetbrains/teamcity-server`

The command runs a server docker container, binds data directory, logs directory and port.

**Upgrade Teamcity Server**

* Execute the Pull Docker Image command.
* Stop the currently running teamcity server container.
* Remove the stopped teamcity server container. 
* Execute the Run Docker Container command. 

#### Teamcity Agent (https://hub.docker.com/r/jetbrains/teamcity-agent/)

Pull Docker image:

`sudo docker pull jetbrains/teamcity-agent`

Run Docker container with Docker daemon enabled:

`sudo docker run -it -e SERVER_URL="192.168.2.244:8111" -v /Users/jlin/teamcity-agent/conf:/data/teamcity_agent/conf -v docker_volumes:/var/lib/docker --privileged -e DOCKER_IN_DOCKER=start --name teamcity-agent jetbrains/teamcity-agent`

The commands run an agent docker container and binds the conf directory. 

Note: when the agent is connected to the server, it is unauthorized. It is necessary to go into the Unauthorized
tab under Agents to authorize it.   

#### ELK on Docker (http://elk-docker.readthedocs.io/)

Pull Docker image:

`sudo docker pull sebp/elk:latest`

Create bridge work in Docker to connect containers.

`sudo docker network create -d bridge elknet`

Run Docker container:

`sudo docker run -p 5601:5601 -p 9200:9200 -p 5044:5044 -e MAX_MAP_COUNT=262144 -it --network=elknet --name elk sebp/elk`

Refer to the prerequisite section in the documentation for details on the MAX_MAP_COUNT environment variable.

#### Filebeat on Docker (https://www.elastic.co/guide/en/beats/filebeat/current/running-on-docker.html)

Pull Docker image:

`docker pull docker.elastic.co/beats/filebeat:6.2.4`

Run Docker container:

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


#### Useful Docker Commands

Remove dangling images

`docker rmi $(docker images -q -f dangling=true)`

#### Build Gradle project in Docker
https://stackoverflow.com/questions/46792438/build-gradle-project-inside-a-docker

#### Dockerized AWS CLI
https://hub.docker.com/r/mesosphere/aws-cli/

Copyright (c) 2018 Joe Lin