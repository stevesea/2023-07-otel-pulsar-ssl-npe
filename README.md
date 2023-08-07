# 2023-07-otel-pulsar-ssl-npe
simple app to reproduce issue with otel instrumentation of a spring boot app with pulsar client to pulsar+ssl endpoint

Saw an NPE in a Spring Boot 3 app in the OTel Netty instrumentation, appears correlated with a Pulsar Consumer/Producer
which connects to a `pulsar+ssl://` endpoint


# how to build & run

```shell 

# start a standalone pulsar instance via docker-compose
docker compose up 

# run the app
mvn clean package spring-boot:run 

# run the app, but use the SSL URI
mvn clean package sprint-boot:run -Dspring-boot.run.profiles=localhost-ssl
```


or, if you want to build a container: 
```shell 
mvn clean package jib:dockerBuild

# if on m1 laptop, set arch via jib.platform-arch property:
mvn compile jib:dockerBuild -Djib.platform-arch=arm64

```

if you want to run via IDE/CLI, the 'important' options are:

add the java agent, and allow access to the sun.net classes

`--add-exports java.base/sun.net=ALL-UNNAMED -javaagent:/Users/schristensen/git/otel-1.28.0.jar`

env vars:

```
OTEL_JAVAAGENT_CONFIGURATION_FILE=src/main/jib/app/otel/otel.properties
```
