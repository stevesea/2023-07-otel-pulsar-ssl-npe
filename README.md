# 2023-07-otel-pulsar-ssl-npe
simple app to reproduce issue with otel instrumentation of a spring boot app with pulsar client to pulsar+ssl endpoint

Saw an NPE in a Spring Boot 3 app in the OTel Netty instrumentation, appears correlated with a Pulsar Consumer/Producer
which connects to a `pulsar+ssl://` endpoint



# the NPE

When the pulsar client is instrumented for a `pulsar+ssl://` URI, the Netty instrumentation logs some NullPointerExceptions like:

```
2023-08-07T15:08:38.506-06:00  WARN  51699 --- [r-client-io-1-1] i.netty.util.concurrent.DefaultPromise   : An exception was thrown by io.opentelemetry.javaagent.shaded.instrumentation.netty.v4.common.internal.client.NettySslInstrumentationHandler$$Lambda$974/0x000000080080bb78.operationComplete()

java.lang.NullPointerException: Cannot invoke "io.opentelemetry.javaagent.shaded.io.opentelemetry.context.Context.with(io.opentelemetry.javaagent.shaded.io.opentelemetry.context.ImplicitContextKeyed)" because "parentContext" is null
        at io.opentelemetry.javaagent.shaded.instrumentation.netty.v4.common.internal.client.NettySslErrorOnlyInstrumenter.start(NettySslErrorOnlyInstrumenter.java:30) ~[opentelemetry-javaagent-1.28.0.jar:na]
        at io.opentelemetry.javaagent.shaded.instrumentation.netty.v4.common.internal.client.NettySslInstrumentationHandler.lambda$connect$0(NettySslInstrumentationHandler.java:102) ~[opentelemetry-javaagent-1.28.0.jar:na]
        at io.netty.util.concurrent.DefaultPromise.notifyListener0(DefaultPromise.java:590) ~[netty-common-4.1.94.Final.jar:4.1.94.Final]
        at io.netty.util.concurrent.DefaultPromise.notifyListeners0(DefaultPromise.java:583) ~[netty-common-4.1.94.Final.jar:4.1.94.Final]
        at io.netty.util.concurrent.DefaultPromise.notifyListenersNow(DefaultPromise.java:559) ~[netty-common-4.1.94.Final.jar:4.1.94.Final]
        at io.netty.util.concurrent.DefaultPromise.notifyListeners(DefaultPromise.java:492) ~[netty-common-4.1.94.Final.jar:4.1.94.Final]
        at io.netty.util.concurrent.DefaultPromise.setValue0(DefaultPromise.java:636) ~[netty-common-4.1.94.Final.jar:4.1.94.Final]
        at io.netty.util.concurrent.DefaultPromise.setSuccess0(DefaultPromise.java:625) ~[netty-common-4.1.94.Final.jar:4.1.94.Final]
        at io.netty.util.concurrent.DefaultPromise.trySuccess(DefaultPromise.java:105) ~[netty-common-4.1.94.Final.jar:4.1.94.Final]
        at io.netty.channel.DefaultChannelPromise.trySuccess(DefaultChannelPromise.java:84) ~[netty-transport-4.1.94.Final.jar:4.1.94.Final]
        at io.netty.channel.nio.AbstractNioChannel$AbstractNioUnsafe.fulfillConnectPromise(AbstractNioChannel.java:300) ~[netty-transport-4.1.94.Final.jar:4.1.94.Final]
        at io.netty.channel.nio.AbstractNioChannel$AbstractNioUnsafe.finishConnect(AbstractNioChannel.java:335) ~[netty-transport-4.1.94.Final.jar:4.1.94.Final]
        at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:776) ~[netty-transport-4.1.94.Final.jar:4.1.94.Final]
        at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:724) ~[netty-transport-4.1.94.Final.jar:4.1.94.Final]
        at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:650) ~[netty-transport-4.1.94.Final.jar:4.1.94.Final]
        at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:562) ~[netty-transport-4.1.94.Final.jar:4.1.94.Final]
        at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:997) ~[netty-common-4.1.94.Final.jar:4.1.94.Final]
        at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74) ~[netty-common-4.1.94.Final.jar:4.1.94.Final]
        at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30) ~[netty-common-4.1.94.Final.jar:4.1.94.Final]
        at java.base/java.lang.Thread.run(Thread.java:833) ~[na:na]

```



# how to build & run

NOTE: we're not actually setting up Pulsar to listen to the pulsar+ssl port. The NPE in the Netty
instrumentation is encountered prior to the TLS handshake; this example is only reproduces the NPE
and doesn't really do anything functional with the pulsar clients.

```shell 

# start a standalone pulsar instance via docker-compose
docker compose up --detach

# run the app, see the NPEs in the OTel instrumentation output.
# you'll see other connection errors (since pulsar isn't actually running SSL).
mvn clean package spring-boot:run 

# if you want to run the app _and_ connect to non-ssl pulsar, use a different spring profile:
mvn clean package sprint-boot:run -Dspring-boot.run.profiles=localhost-nossl
```


or, if you want to build/run with a container: 
```shell 
mvn compile jib:dockerBuild

# if on m1 laptop, set arch via jib.platform-arch property:
#mvn compile jib:dockerBuild -Djib.platform-arch=arm64

# run the app in container. SPRING_PROFILES_ACTIVE env var is set to 'docker-ssl' by default
docker run -p 8080:8080 --rm -it --network=mynet otel-pulsar-ssl-npe:0.0.1-SNAPSHOT


```

if you want to run via IDE/CLI, add the java agent, and allow access to the sun.net classes

`--add-exports java.base/sun.net=ALL-UNNAMED -javaagent:/Users/schristensen/git/otel-1.28.0.jar`

env vars:

```
OTEL_JAVAAGENT_CONFIGURATION_FILE=src/main/jib/app/otel/otel.properties


SPRING_PROFILES_ACTIVE=<your desired profile>
```

## spring profiles

at the moment, the spring profiles just pick different pulsar URIs.

when running the container, run it within the `mynet` network and use one of the `docker-*` profiles  

| profile         | pulsar URI                   |
|-----------------|------------------------------|
| <default>       | pulsar+ssl://localhost:6651  |
| localhost-nossl | pulsar://localhost:6650      |
| docker-ssl      | pulsar+ssl://pulsar:6651     |
| docker-nossl    | pulsar://pulsar:6650         |

## otel config

agent config file is at `src/main/jib/app/otel/otel.properties`

generally, the app is pointed to that via env var: `OTEL_JAVAAGENT_CONFIGURATION_FILE=src/main/jib/app/otel/otel.properties`
