package com.example.otelpulsarsslnpe;

import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.CompressionType;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PulsarConfig {
    private static final Logger logger = LoggerFactory.getLogger(PulsarConfig.class);
    @Value("${pulsar.service-url}")
    private String serviceUrl;

    @Bean
    public PulsarClient pulsarClient() throws PulsarClientException {
        return PulsarClient.builder()
                .authentication(AuthenticationFactory.token("intentionally invalid token"))
                .serviceUrl(serviceUrl)
                .enableTlsHostnameVerification(false)
                .allowTlsInsecureConnection(false)
                .build();
    }

    @Bean
    public Consumer<String> consumer(PulsarClient client, MyListener listener)
            throws PulsarClientException {
        return client.newConsumer(Schema.STRING)
                .messageListener(listener)
                .topic("my-topic-consumer")
                .subscriptionType(SubscriptionType.Shared)
                .subscriptionName("myapp")
                .subscribe();
    }

    @Bean
    public Producer<String> producer(PulsarClient client) throws PulsarClientException {
        return client.newProducer(Schema.STRING)
                .topic("my-topic-producer")
                .compressionType(CompressionType.LZ4)
                .create();

    }

}
