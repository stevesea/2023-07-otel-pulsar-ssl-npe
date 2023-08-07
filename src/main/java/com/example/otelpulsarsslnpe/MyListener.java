package com.example.otelpulsarsslnpe;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.PulsarClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class MyListener implements MessageListener<String> {
    private static final Logger logger = LoggerFactory.getLogger(MyListener.class);

    @Override
    public void received(Consumer<String> consumer, Message<String> msg) {

        logger.info("Received message: {}", msg.getValue());
        try {
            consumer.acknowledge(msg);
        } catch (PulsarClientException e) {
            logger.error("error acking message", e);
        }
    }
}
