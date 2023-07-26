# 2023-07-otel-pulsar-ssl-npe
simple app to reproduce issue with otel instrumentation of a spring boot app with pulsar client to pulsar+ssl endpoint

Saw an NPE in a Spring Boot 3 app in the OTel Netty instrumentation, appears correlated with a Pulsar Consumer/Producer
which connects to a `pulsar+ssl://` endpoint
