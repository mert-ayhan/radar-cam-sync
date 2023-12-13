package com.github.mertayhan.radarcamsync.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaProducerService {

    private static KafkaProducerService instance;
    private Producer<String, String> producer;

    private KafkaProducerService() {
        Properties kafkaProperties = KafkaConfig.loadKafkaProperties();
        this.producer = new KafkaProducer<>(kafkaProperties);

        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    public static synchronized KafkaProducerService getInstance() {
        if (instance == null) {
            instance = new KafkaProducerService();
        }
        return instance;
    }

    public void sendMessage(String topic, String key, String value) {
        producer.send(new ProducerRecord<>(topic, key, value));
    }

    public void close() {
        producer.close();
    }
}
