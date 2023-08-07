package com.example.otelpulsarsslnpe;

import org.apache.pulsar.client.api.Producer;
import org.springframework.stereotype.Component;

@Component
class MyProducer {
    private final Producer<String> producer;

    public MyProducer(Producer<String> producer) {
        this.producer = producer;
    }

    public void send(String msg) {
        producer.sendAsync(msg);
    }
}
